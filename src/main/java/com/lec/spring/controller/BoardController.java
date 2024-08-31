package com.lec.spring.controller;

import com.lec.spring.domain.Post;
import com.lec.spring.domain.PostValidator;
import com.lec.spring.service.BoardService;
import com.lec.spring.utill.U;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;
    public BoardController(){
        System.out.println(getClass().getName() + "() 생성");
    }
    @GetMapping("/write")
    public void write(){}

    @PostMapping("/write")
    public String writeOk(
            @RequestParam Map<String, MultipartFile> files, // 첨부파일들
            @ModelAttribute("post")
            @Valid // @Valid: binding 시 Validator 객체가 검증수행케 함.
            Post post
            ,BindingResult result   // Validator 가 유효성 검사를 수행한 결과가 담긴 객체
            ,Model model
            ,RedirectAttributes redirectAttrs   // redirect 시 넘겨줄 값들
    ){
        // validation 과정에서 에러가 있었다면 redirect 할거다!
        if (result.hasErrors()){
            // redirect 시 기좀에 입력했던 값들은 보이게 하기
            redirectAttrs.addFlashAttribute("user", post.getUser());
            redirectAttrs.addFlashAttribute("subject", post.getSubject());
            redirectAttrs.addFlashAttribute("content", post.getContent());

            List<FieldError> errList = result.getFieldErrors();
            for (FieldError err : errList){
                System.out.println(err.getField() + " : " + err.getCode());
                redirectAttrs.addFlashAttribute("error_"+err.getField(), err.getCode());
            }
            return "redirect:/board/write";  //GET
        }

        int write = boardService.write(post, files);
        model.addAttribute("result", write);

        return "board/writeOK";
    }


    @GetMapping("/detail/{id}")
    public String detail(@PathVariable long id, Model model){
        model.addAttribute("post", boardService.detail(id));
        return "board/detail";
    }


    @GetMapping("/list")
    //public void list(Model model){
    public void list(Integer page, Model model){
        model.addAttribute("list", boardService.list(page, model));
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable long id, Model model){
        model.addAttribute("post", boardService.selectById(id));
        return "board/update";
    }
    @PostMapping("/update")
    public String updateOk(
            @Valid Post post
            , @RequestParam Map<String, MultipartFile> files    // 새로 추가될 첨부파일들
            , Long[] delfile    // 삭제될 파일들
            , BindingResult result
            , Model model   // 매개변수 선언시 BindingResult 보다 Model 을 뒤에 두어야 한다.
            , RedirectAttributes redirectAttrs
    ){
        // validation 과정에서 에러가 있었다면 redirect 할거다!
        if (result.hasErrors()){
            // redirect 시 기좀에 입력햇던 값들은 보이게 하기
            redirectAttrs.addFlashAttribute("subject", post.getSubject());
            redirectAttrs.addFlashAttribute("content", post.getContent());

            List<FieldError> errList = result.getFieldErrors();
            for (FieldError err : errList){
                System.out.println(err.getField() + " : " + err.getCode());
                redirectAttrs.addFlashAttribute("error_" + err.getField(), err.getCode());
            }

            return "redirect:/board/update" + post.getId(); //GET
        }

        model.addAttribute("result", boardService.update(post, files, delfile));
        return "board/updateOk";
    }

    @PostMapping("/delete")
    public String deleteOk(long id, Model model){
        model.addAttribute("result", boardService.deleteById(id));
        return "board/deleteOk";
    }

    @InitBinder // 이 컨트롤러 클래스의 handler 에서 폼 데이터를 바인딩 할때 검증하는 Validator 객체 지정
    public void initBinder(WebDataBinder binder){
        System.out.println("initBinder() 호출");
        binder.setValidator(new PostValidator());
    }

    // 페이징
    // pageRows 변경시 동작
    @PostMapping("/pageRows")
    public String pageRows(Integer page, Integer pageRows){
        U.getSession().setAttribute("pageRows", pageRows);
        return "redirect:/board/list?page=" + page;
    }


}
