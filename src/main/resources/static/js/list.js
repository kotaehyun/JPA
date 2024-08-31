$(function(){
    $("[name='pageRows']").change(function(){
//        alert($(this).val());   // 확인용
       var frm = $("[name='frmPageRows']");
       frm.attr("method", "POST");
       frm.attr("action", "pageRows");   // ->  /board/list => /board/pageRows
       frm.submit();
    });
});