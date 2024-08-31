package com.lec.spring.service;


import com.lec.spring.domain.Attachment;
import com.lec.spring.domain.Post;
import com.lec.spring.domain.User;
import com.lec.spring.repository.AttachmentRepository;
import com.lec.spring.repository.PostRepository;
import com.lec.spring.repository.UserRepository;
import com.lec.spring.utill.U;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

// Service
// - 비즈니스 로직
// - 트랜잭션

@Service
public class BoardServiceImpl implements BoardService {


    @Value(("${app.upload.path}"))
    private String uploadDir;
    @Value("${app.pagination.write_pages}")
    private int WRITE_PAGES;
    @Value("${app.pagination.page_rows}")
    private int PAGE_ROWS;

    private PostRepository postRepository;
    private UserRepository userRepository;
    private AttachmentRepository attachmentRepository;

    @Autowired
    public void setPostRepository(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setAttachmentRepository(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    @Autowired
    public BoardServiceImpl(){
        System.out.println("BoardService() 생성");
    }

    // 글 작성
    @Override
    public int write(Post post) {   // post 안에는 subject, content 가 담겨 있다.

        // 현재 로그인한 작성자 정보
        User user = U.getLoggedUser();

        // 위 정보는 session 의 정보이고, 이단 DB에서 다시 읽어온다.
        user = userRepository.findById(user.getId()).orElse(null);
        post.setUser(user);     // 글 작성자 세팅!

        // DB 에 저장 -> repository
        postRepository.saveAndFlush(post);

        return 1;
    }

    @Override
    public int write(Post post, Map<String, MultipartFile> files){
        // 현재 로그인한 작성자 정보
        User user = U.getLoggedUser();

        // 위 정보는 session 의 정보 이고,  일단 DB 에서 다시 읽어온다
        user = userRepository.findById(user.getId()).orElse(null);
        post.setUser(user);  // 글 작성자 세팅!

        // DB 에 저장 -> repository
        postRepository.save(post);

        // 첨부파일 추가
        addFiles(files, post.getId());

        return 1;
    }

    // 특정 글(id) 첨부파일(들) 추가
    private void addFiles(Map<String, MultipartFile> files, Long id) {
        if(files != null){
            for(var e : files.entrySet()){

                // name="upfile" 인 경우만 첨부파일 등록. (이유, 다른 웹에디터와 섞이지 않도록)
                if(!e.getKey().startsWith("upfile")) continue;

                // 첨부파일 정보 출력
                System.out.println("\n첨부파일 정보: " + e.getKey());  // name값
                U.printFileInfo(e.getValue());
                System.out.println();

                // 물리적인 파일 저장
                Attachment file = upload(e.getValue());

                // 성공하면 DB 에도 저장
                if(file != null){
                     file.setPost(id);    // FK 설정
                    //file.setPost(postRepository.findById(id).orElse(null));    // FK 설정
                    attachmentRepository.save(file);   // INSERT
                }
            }
        }
    } // end addFile()

    // 물리적인 파일 저장, 중복된 이름 rename 처리
    private Attachment upload(MultipartFile multipartFile){
        Attachment attachment = null;

        // 담겨 있는 파일이 없으면 pass~
        String originalFilename = multipartFile.getOriginalFilename();
        if(originalFilename == null || originalFilename.length() == 0) return null;

        // 원본파일명 ( clean )
        String sourceName = StringUtils.cleanPath(originalFilename);

        // 저장될 파일명
        String fileName = sourceName;

        // 파일이 중복되는지 확인
        File file = new File(uploadDir + File.separator + sourceName);
        if(file.exists()){  // 이미 존재하는 파일명!  중복된다면 다른 이름으로 변경하여 저장 시도
            // a.txt => a_2378142783946.txt  : time stamp 값을 활용할거다!

            int pos = fileName.lastIndexOf(".");
            if(pos > -1){  // 확장자가 있는 파일의 경우
                String name = fileName.substring(0, pos);   // 파일 '이름'
                String ext = fileName.substring(pos  + 1);   // 파일 '확장자'

                // 중복방지를 위한 새로운 이름 (현재시간 ms) 를 파일명에 추가
                fileName = name + "_" + System.currentTimeMillis() + "." + ext;
            } else {   // 확장자가 없는 파일의 경우
                fileName += "_" + System.currentTimeMillis();
            }
        }
        //  저장할 파일명
        System.out.println("fileName: " + fileName);

        // java.nio.*
        Path copyOfLocation = Paths.get(new File(uploadDir + File.separator + fileName).getAbsolutePath());
        System.out.println(copyOfLocation);

        try{
            Files.copy(   // 물리적으로 저장
                    multipartFile.getInputStream(),
                    copyOfLocation,
                    StandardCopyOption.REPLACE_EXISTING    // 기존에 존재하면 덮어쓰기
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        attachment = Attachment.builder()
                .filename(fileName)     // 저장된 이름
                .sourcename(sourceName)  // 원본이름
                .build();

        return attachment;
    } // end load();

    // 특정 id 의 글 조회
    // 트랜잭션 처리
    // 1. 조회수 증가 (UPDATE)
    // 2. 글 읽엉오기 (SELECT)
    @Override
    @Transactional  // <- 이메소드를 트랜잭션으로 처리
    public Post detail(Long id) {

        Post post = postRepository.findById(id).orElse(null);

        if(post != null){
            // 조회수 증가
            post.setViewCnt(post.getViewCnt() + 1);
            postRepository.saveAndFlush(post);  // UPDATE

            // 첨부파일(들) 정보 가져오기
            List<Attachment> fileList = attachmentRepository.findByPost(post.getId());
            // 이미지 파일 여부 세팅
            setImage(fileList);

            post.setFileList(fileList);
        }

        return post;
    }

    private void setImage(List<Attachment> fileList){
        // upload 실제 물리적인 경로
        String realPath = new File(uploadDir).getAbsolutePath();

        for(Attachment attachment : fileList){
            BufferedImage imgData = null;
            File f = new File(realPath, attachment.getFilename());   // 첨부파일에 대한 File 객체
            try{
                imgData = ImageIO.read(f);   // 이미지가 아니면 null 리턴
            } catch (IOException e) {
                System.out.println("파일존재안함: " + f.getAbsolutePath() + " [" + e.getMessage() + "]");
            }
            if(imgData != null) attachment.setImage(true);
        }
    }

    // 글 목록
    @Override
    public List<Post> list() { return postRepository.findAll(); }

    // 페이징 리스트
    @Override
    public List<Post> list(Integer page, Model model) {
        // 현재 페이지 parameter
        if(page == null) page = 1; // 디폴트는 1 page
        if(page < 1) page = 1;

        // 페이징
        // writePages: 한 [페이징] 당 몇개의 페이지가 표시되나
        // pageRows: 한 '페이지'에 몇개의 글을 리스트 할것인가?
        HttpSession session = U.getSession();
        Integer writePages =  (Integer)session.getAttribute("writePage");
        if (writePages == null) writePages = WRITE_PAGES;   // session 에 저장된 값이 없으면 기본값으로 동작
        Integer pageRows = (Integer)session.getAttribute("pageRows");
        if (pageRows == null) pageRows = PAGE_ROWS;

        session.setAttribute("page", page); // 현재 페이지 번호 -> session 에 저장

        // Caution! PageRequest.of(page, ..) The page value is 0-base!
        Page<Post> pageWrites = postRepository.findAll(PageRequest.of(page -1, pageRows, Sort.by(Sort.Order.desc("id"))));

        long cnt = pageWrites.getTotalElements(); // 글 목록 전체의 개수
        int totalPage = pageWrites.getTotalPages(); // 총 몇 '페이지' 분량?

        // page 값 보정
        if(page > totalPage) page = totalPage;

        // fromRow 계산 (몇번째 데이터부터?)
        int fromRow = (page - 1) * pageRows;

        // [페이징] 에 표시할 '시작페이지' 와 '마지막페이지' 계산
        int startPage = (((page - 1) / writePages) * writePages) + 1;
        int endPage = startPage + writePages - 1;
        if (endPage >= totalPage) endPage = totalPage;

        model.addAttribute("cnt", cnt);  // 전체 글 개수
        model.addAttribute("page", page);   // 현재 페이지
        model.addAttribute("totalPage", totalPage);  // 총 '페이지' 수
        model.addAttribute("pageRows", pageRows);  // 한 '페이지' 에 표시할 글 대수

        // [페이징]
        model.addAttribute("url", U.getRequest().getRequestURI());  // 목록 url
        model.addAttribute("writePages", writePages); // [페이징] 에 표시할 숫자 개수
        model.addAttribute("startPage", startPage); // [페이징] 에 표시할 시작 개수
        model.addAttribute("endPage", endPage); // [페이징] 에 표시할 마지막 개수

        // 해당 페이지의 글 목록 읽어오기
        List<Post> list = pageWrites.getContent();
        model.addAttribute("list",list);

        return list;
    }


    // 특정 id의 글 읽어오기 (SELECT)
    // 조회수 증가 없슴
    @Override
    public Post selectById(long id) {
        Post post = postRepository.findById(id).orElse(null);

        if(post != null){
            // 첨부파일 정보 가져오기
            List<Attachment> fileList = attachmentRepository.findByPost(post.getId());
            setImage(fileList);  // 이미지 파일 여부 세팅
            post.setFileList(fileList);
        }

        return post;
    }


    // 특정 id 글 수정하기 (제목, 내용) (UPDATE)
    @Override
    public int update(Post post) {
        postRepository.save(post);
        return 1;
    }

    @Override
    public int update(Post post, Map<String, MultipartFile> files, Long[] delfile) {
        int result = 0;

        // You have to read what you want to update first.
        Post p =  postRepository.findById(post.getId()).orElse(null);
        if (p != null){
            p.setSubject(post.getSubject());
            p.setContent(post.getContent());
            p = postRepository.save(p); // UPDATE


        // 첨부파일 추가
        addFiles(files, post.getId());

        // 삭제할 첨부차일들을 삭제하기
        if (delfile != null){
            for (Long fileId : delfile){
                Attachment file = attachmentRepository.findById(fileId).orElse(null);
                if (file != null){
                    delFile(file);  // 물리적 삭제
                    attachmentRepository.delete(file);  // DB에서 삭제

                    }
                }
            }

            result = 1;


        }


        return result;

    }

    // 특정 첨부파일 물리적으로 삭제
    private void delFile(Attachment file) {
        String saveDirectory = new File(uploadDir).getAbsolutePath();

        File f = new File(saveDirectory, file.getFilename());  // 물리적으로 저장된 파일의 File 객체
        System.out.println("삭제시도---> " + f.getAbsolutePath());

        if(f.exists()){
            if (f.delete()) { // 삭제!
                System.out.println("삭제 성공");
            } else {
                System.out.println("삭제 실패");
            }
        } else {
            System.out.println("파일이 존재하지 않습니다.");
        }
    }

    // 특정 id의 글 삭제하기 (DELETE)
    @Override
    public int deleteById(long id) {
        int result = 0;

        Post post = postRepository.findById(id).orElse(null);
        if(post != null){
            // 물리적으로 저장된 첨부파일(들) 삭제
            List<Attachment> fileList = attachmentRepository.findByPost(id);
            if (fileList != null && fileList.size() > 0){
                for (var file : fileList){
                    delFile(file);
                }
            }

            // 글삭제 (참조하는 첨부파일, 댓글 등도 같이 삭제될것이다. ON DELETE CASCADE)
            postRepository.delete(post);
            result = 1;
        }
        return result;
    }

}
