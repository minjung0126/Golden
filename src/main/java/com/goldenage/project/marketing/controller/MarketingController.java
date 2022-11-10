package com.goldenage.project.marketing.controller;

import com.goldenage.project.marketing.model.dto.MarketingDTO;
import com.goldenage.project.marketing.model.dto.MkItemDTO;
import com.goldenage.project.marketing.model.dto.MkMdDTO;
import com.goldenage.project.marketing.model.dto.MkPosterDTO;
import com.goldenage.project.marketing.model.service.MarketingServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/marketing/*")
public class MarketingController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final MarketingServiceImpl marketingService;


    @Autowired
    public MarketingController(MarketingServiceImpl marketingService){

        this.marketingService = marketingService;
    }


    @GetMapping("/list")
    public ModelAndView marketingList(HttpServletRequest request, ModelAndView mv){

        List<MarketingDTO> marketingList = marketingService.selectAllMarketing();

        mv.addObject("marketingList", marketingList);
        mv.setViewName("marketing/marketing");

        return mv;
    }

    @GetMapping("/detail")
    public ModelAndView marketingDetail(ModelAndView mv, HttpServletRequest request, @RequestParam(value = "mkNum", required = false) String mkNum){

        String num = request.getParameter("mkNum");
        log.info("mkNum : " + mkNum);

        MarketingDTO marketingDetail = marketingService.selectOneMarketing(mkNum);
        List<MkPosterDTO> mkPosterList = marketingService.selectAllPoster(mkNum);
        List<MkMdDTO> mkMdList = marketingService.selectAllMd(mkNum);
        List<MkItemDTO> mkItemList = marketingService.selectAllItem(mkNum);

        mv.addObject("marketingDetail", marketingDetail);
        mv.addObject("mkPosterList", mkPosterList);
        mv.addObject("mkMdList", mkMdList);
        mv.addObject("mkItemList", mkItemList);

        mv.setViewName("marketing/marketing_detail");
        return mv;
    }

    @GetMapping("/detail/regist")
    public ModelAndView marketingDetailNew(ModelAndView mv, HttpServletRequest request){

        mv.setViewName("marketing/marketing_detail_new");
        return mv;
    }

    @PostMapping("/detail/regist")
    public String marketingDetailNew(@ModelAttribute MarketingDTO marketing, @RequestParam(value="file", required = false) MultipartFile file, RedirectAttributes rttr) throws Exception{

        System.out.println("테스트용 : " + marketing);
        System.out.println("사진 테스트 : " + file);

        String root = ResourceUtils.getURL("src/main/resources").getPath();

        String filePath = root + "static/images/marketing";

        log.info("루트ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ" + filePath);

        File mkdir = new File(filePath);
        if(!mkdir.exists()) {
            mkdir.mkdirs();
        }

        String originFileName = "";
        String ext = "";
        String changeName = "";

        if(file.getSize() > 0) {
            originFileName = file.getOriginalFilename();
            ext = originFileName.substring(originFileName.lastIndexOf("."));
            changeName = UUID.randomUUID().toString().replace("-",  "");


            marketing.setMkOriMain(originFileName);
            marketing.setMkFileMain(changeName + ext);

            System.out.println("다시출력 : " + marketing);

            // 공연 등록
            marketingService.insertMkInfo(marketing);


            try {
//                file.transferTo(new File(filePath + "\\" + changeName + ext));
                file.transferTo(new File(filePath + mkdir.separator + changeName + ext));
            } catch (IOException e) {
                e.printStackTrace();
//                new File(filePath + "\\" + changeName + ext).delete();
                new File(filePath + mkdir.separator + changeName + ext).delete();
            }
        } else {
            marketingService.insertMkInfo(marketing);
        }

        rttr.addFlashAttribute("message", "게시물이 등록되었습니다.");
        rttr.addFlashAttribute("check", "success");

        return "redirect:/marketing/list";
    }

    @GetMapping("/detail/new/cast")
    public ModelAndView marketingDetailNewCast(@ModelAttribute MkPosterDTO mkPoster, ModelAndView mv, HttpServletRequest request){

        mv.setViewName("marketing/marketing_new_cast");
        return mv;
    }

    @PostMapping("/detail/new/cast")
    public String marketingDetailNewCast(@RequestParam(value="num", required= false) String mkNum, @ModelAttribute MkPosterDTO mkPoster, @RequestParam(value="file", required = false) MultipartFile file, RedirectAttributes rttr) throws Exception{

        System.out.println(mkNum);
        mkPoster.setMkNum(Integer.parseInt(mkNum));

        System.out.println("테스트용 : " + mkPoster);
        System.out.println("사진 테스트 : " + file);

        String root = ResourceUtils.getURL("src/main/resources").getPath();

        String filePath = root + "/static/images/marketing/poster";

        log.info("루트ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ" + filePath);

        File mkdir = new File(filePath);
        if(!mkdir.exists()) {
            mkdir.mkdirs();
        }

        String originFileName = "";
        String ext = "";
        String changeName = "";

        if(file.getSize() > 0) {
            originFileName = file.getOriginalFilename();
            ext = originFileName.substring(originFileName.lastIndexOf("."));
            changeName = UUID.randomUUID().toString().replace("-",  "");

            mkPoster.setpOriName(originFileName);
            mkPoster.setpFileName(changeName + ext);

            System.out.println("다시출력 : " + mkPoster);

            // 공연 등록
            marketingService.insertMkPoster(mkPoster);

            try {
                file.transferTo(new File(filePath + mkdir.separator + changeName + ext));
            } catch (IOException e) {
                e.printStackTrace();
                new File(filePath + mkdir.separator + changeName + ext).delete();
            }
        } else {
            marketingService.insertMkPoster(mkPoster);
        }

        rttr.addFlashAttribute("message", "등록이 완료되었습니다.");
        rttr.addFlashAttribute("check", "success");

        return "redirect:/marketing/detail/modify?mkNum=" + mkNum;
    }

    @GetMapping("/detail/new/md")
    public ModelAndView marketingDetailNewMd(ModelAndView mv, HttpServletRequest request){

        mv.setViewName("marketing/marketing_new_md");
        return mv;
    }

    @PostMapping("/detail/new/md")
    public String marketingDetailNewMd(@RequestParam(value="num", required= false) String mkNum, @ModelAttribute MkMdDTO mkMd, @RequestParam(value="file", required = false) MultipartFile file, RedirectAttributes rttr) throws Exception{

        System.out.println(mkNum);
        mkMd.setMkNum(Integer.parseInt(mkNum));

        System.out.println("테스트용 : " + mkMd);
        System.out.println("사진 테스트 : " + file);

        String root = ResourceUtils.getURL("src/main/resources").getPath();

        String filePath = root + "static/images/marketing/md";

        log.info("루트ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ" + filePath);

        File mkdir = new File(filePath);
        if(!mkdir.exists()) {
            mkdir.mkdirs();
        }

        String originFileName = "";
        String ext = "";
        String changeName = "";

        if(file.getSize() > 0) {
            originFileName = file.getOriginalFilename();
            ext = originFileName.substring(originFileName.lastIndexOf("."));
            changeName = UUID.randomUUID().toString().replace("-",  "");


            mkMd.setMdOriName(originFileName);
            mkMd.setMdFileName(changeName + ext);

            System.out.println("다시출력 : " + mkMd);

            // md 등록
            marketingService.insertMkMd(mkMd);


            try {
                file.transferTo(new File(filePath + mkdir.separator + changeName + ext));
            } catch (IOException e) {
                e.printStackTrace();
                new File(filePath + mkdir.separator + changeName + ext).delete();
            }
        } else {
            marketingService.insertMkMd(mkMd);
        }

        rttr.addFlashAttribute("message", "등록이 완료되었습니다.");
        rttr.addFlashAttribute("check", "success");

        return "redirect:/marketing/detail/modify?mkNum=" + mkNum;
    }

    @GetMapping("/detail/new/item")
    public ModelAndView marketingDetailNewItem(ModelAndView mv, HttpServletRequest request){

        mv.setViewName("marketing/marketing_new_item");
        return mv;
    }

    @PostMapping("/detail/new/item")
    public String marketingDetailNewItem(@RequestParam(value="num", required= false) String mkNum, @ModelAttribute MkItemDTO mkItem, @RequestParam(value="file", required = false) MultipartFile file, RedirectAttributes rttr) throws Exception{

        System.out.println(mkNum);
        mkItem.setMkNum(Integer.parseInt(mkNum));

        System.out.println("테스트용 : " + mkItem);
        System.out.println("사진 테스트 : " + file);

        String root = ResourceUtils.getURL("src/main/resources").getPath();

        String filePath = root + "static/images/marketing/item";

        log.info("루트ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ" + filePath);

        File mkdir = new File(filePath);
        if(!mkdir.exists()) {
            mkdir.mkdirs();
        }

        String originFileName = "";
        String ext = "";
        String changeName = "";

        if(file.getSize() > 0) {
            originFileName = file.getOriginalFilename();
            ext = originFileName.substring(originFileName.lastIndexOf("."));
            changeName = UUID.randomUUID().toString().replace("-",  "");


            mkItem.setItemOriName(originFileName);
            mkItem.setItemFileName(changeName + ext);

            System.out.println("다시출력 : " + mkItem);

            // item 등록
            marketingService.insertMkItem(mkItem);

            try {
                file.transferTo(new File(filePath + mkdir.separator + changeName + ext));
            } catch (IOException e) {
                e.printStackTrace();
                new File(filePath + mkdir.separator + changeName + ext).delete();
            }
        } else {
            marketingService.insertMkItem(mkItem);
        }

        rttr.addFlashAttribute("message", "등록이 완료되었습니다.");
        rttr.addFlashAttribute("check", "success");

        return "redirect:/marketing/detail/modify?mkNum=" + mkNum;
    }

    @GetMapping("/detail/modify")
    public ModelAndView marketingDetailModify(ModelAndView mv, HttpServletRequest request, @RequestParam(value = "mkNum", required = false) String mkNum){

        MarketingDTO marketingDetail = marketingService.selectOneMarketing(mkNum);
        List<MkPosterDTO> mkPosterList = marketingService.selectAllPoster(mkNum);
        List<MkMdDTO> mkMdList = marketingService.selectAllMd(mkNum);
        List<MkItemDTO> mkItemList = marketingService.selectAllItem(mkNum);

        mv.addObject("marketingDetail", marketingDetail);
        mv.addObject("mkPosterList", mkPosterList);
        mv.addObject("mkMdList", mkMdList);
        mv.addObject("mkItemList", mkItemList);

        mv.setViewName("marketing/marketing_detail_modify");
        return mv;
    }

    @PostMapping("/detail/modify")
    public String marketingDetailModify(@ModelAttribute MarketingDTO marketing, @RequestParam(value = "mkNum", required = false) String mkNum, @RequestParam(value="file", required = false) MultipartFile file, RedirectAttributes rttr) throws Exception{

        log.info("MarketingDTO : " + marketing);
        log.info("아이템넘버 : " + mkNum);
        log.info("MarketingDTO file : " + file);

        String root = ResourceUtils.getURL("src/main/resources").getPath();

        String filePath = root + "static/images/marketing";


        log.info("루트ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ" + filePath);

        File mkdir = new File(filePath);
        if(!mkdir.exists()) {
            mkdir.mkdirs();
        }

        String originFileName = "";
        String ext = "";
        String changeName = "";

        marketing.setMkNum(Integer.parseInt(mkNum));
        marketingService.updateMkInfoNoFile(marketing);

        if(file.getSize() > 0) {
            originFileName = file.getOriginalFilename();
            ext = originFileName.substring(originFileName.lastIndexOf("."));
            changeName = UUID.randomUUID().toString().replace("-", "");

            marketing.setMkOriMain(originFileName);
            marketing.setMkFileMain(changeName + ext);

            marketingService.updateMkInfo(marketing);

            try {
                file.transferTo(new File(filePath + mkdir.separator + changeName + ext));
            } catch (IOException e) {

                e.printStackTrace();
                new File(filePath + mkdir.separator + changeName + ext).delete();
            }
        }

        rttr.addFlashAttribute("message", "게시물이 수정되었습니다.");
        rttr.addFlashAttribute("check", "success");

        return "redirect:/marketing/list";
    }

    @GetMapping("/detail/delete")
    public String marketingDelete(@RequestParam(value="mkNum", required = false) String mkNum, RedirectAttributes rttr) throws Exception{

        marketingService.deleteMkInfo(mkNum);

        rttr.addFlashAttribute("message", "게시물이 삭제되었습니다.");
        rttr.addFlashAttribute("check", "success");

        return "redirect:/marketing/list";
    }
    @GetMapping("/detail/modify/cast")
    public ModelAndView marketingDetailModifyCast(ModelAndView mv, @RequestParam(value = "pFileNum", required = false) String pFileNum,HttpServletRequest request){

        MkPosterDTO poster = marketingService.selectPoster(pFileNum);
        mv.addObject("poster", poster);
        mv.setViewName("marketing/marketing_modify_cast");
        return mv;
    }

    @PostMapping("/detail/modify/cast")
    public String marketingModifyCast(@ModelAttribute MkPosterDTO mkPoster, @RequestParam(value = "pFileNum", required = false) String pFileNum, @RequestParam(value="file", required = false) MultipartFile file, RedirectAttributes rttr) throws Exception{

        int mkNum = mkPoster.getMkNum();

        log.info("MarketingDTO : " + mkPoster);
        log.info("아이템넘버 : " + pFileNum);
        log.info("MarketingDTO file : " + file);

        String root = ResourceUtils.getURL("src/main/resources").getPath();

        String filePath = root + "static/images/marketing/poster";


        log.info("루트ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ" + filePath);

        File mkdir = new File(filePath);
        if(!mkdir.exists()) {
            mkdir.mkdirs();
        }

        String originFileName = "";
        String ext = "";
        String changeName = "";

        mkPoster.setpFileNum(Integer.parseInt(pFileNum));
        marketingService.updateMkPosterNoFile(mkPoster);

        if(file.getSize() > 0) {
            originFileName = file.getOriginalFilename();
            ext = originFileName.substring(originFileName.lastIndexOf("."));
            changeName = UUID.randomUUID().toString().replace("-", "");

            mkPoster.setpOriName(originFileName);
            mkPoster.setpFileName(changeName + ext);

            marketingService.updateMkPoster(mkPoster);

            try {
                file.transferTo(new File(filePath + mkdir.separator + changeName + ext));
            } catch (IOException e) {

                e.printStackTrace();
                new File(filePath + mkdir.separator + changeName + ext).delete();
            }
        }

        rttr.addFlashAttribute("message", "수정이 완료되었습니다.");
        rttr.addFlashAttribute("check", "success");

        return "redirect:/marketing/detail/modify?mkNum=" + mkNum;
    }

    @GetMapping("/detail/modify/md")
    public ModelAndView marketingDetailModifyMd(ModelAndView mv, @RequestParam(value = "mdFileNum", required = false) String mdFileNum, HttpServletRequest request){

        MkMdDTO md = marketingService.selectMd(mdFileNum);
        mv.addObject("md", md);
        mv.setViewName("marketing/marketing_modify_md");
        return mv;
    }

    @PostMapping("/detail/modify/md")
    public String marketingModifyMd(@ModelAttribute MkMdDTO mkMd, @RequestParam(value = "mdFileNum", required = false) String mdFileNum, @RequestParam(value="file", required = false) MultipartFile file, RedirectAttributes rttr) throws Exception{

        int mkNum = mkMd.getMkNum();

        log.info("MarketingDTO : " + mkMd);
        log.info("아이템넘버 : " + mdFileNum);
        log.info("MarketingDTO file : " + file);

        String root = ResourceUtils.getURL("src/main/resources").getPath();

        String filePath = root + "static/images/marketing/md";


        log.info("루트ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ" + filePath);

        File mkdir = new File(filePath);
        if(!mkdir.exists()) {
            mkdir.mkdirs();
        }

        String originFileName = "";
        String ext = "";
        String changeName = "";

        mkMd.setMdFileNum(Integer.parseInt(mdFileNum));
        marketingService.updateMkMdNoFile(mkMd);

        if(file.getSize() > 0) {
            originFileName = file.getOriginalFilename();
            ext = originFileName.substring(originFileName.lastIndexOf("."));
            changeName = UUID.randomUUID().toString().replace("-", "");

            mkMd.setMdOriName(originFileName);
            mkMd.setMdFileName(changeName + ext);

            marketingService.updateMkMd(mkMd);

            try {
                file.transferTo(new File(filePath + mkdir.separator + changeName + ext));
            } catch (IOException e) {

                e.printStackTrace();
                new File(filePath + mkdir.separator + changeName + ext).delete();
            }
        }

        rttr.addFlashAttribute("message", "수정이 완료되었습니다.");
        rttr.addFlashAttribute("check", "success");

        return "redirect:/marketing/detail/modify?mkNum=" + mkNum;
    }

    @GetMapping("/detail/modify/item")
    public ModelAndView marketingDetailModifyItem(ModelAndView mv, @RequestParam(value = "itemFileNum", required = false) String itemFileNum, HttpServletRequest request){

        MkItemDTO item = marketingService.selectItem(itemFileNum);
        mv.addObject("item", item);
        mv.setViewName("marketing/marketing_modify_item");
        return mv;
    }

    @PostMapping("/detail/modify/item")
    public String marketingModifyItem(@ModelAttribute MkItemDTO mkItem, @RequestParam(value = "itemFileNum", required = false) String itemFileNum, @RequestParam(value="file", required = false) MultipartFile file, RedirectAttributes rttr) throws Exception{

        int mkNum = mkItem.getMkNum();

        log.info("MarketingDTO : " + mkItem);
        log.info("아이템넘버 : " + itemFileNum);
        log.info("MarketingDTO file : " + file);

        String root = ResourceUtils.getURL("src/main/resources").getPath();

        String filePath = root + "static/images/marketing/item";


        log.info("루트ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ" + filePath);

        File mkdir = new File(filePath);
        if(!mkdir.exists()) {
            mkdir.mkdirs();
        }

        String originFileName = "";
        String ext = "";
        String changeName = "";

        mkItem.setItemFileNum(Integer.parseInt(itemFileNum));
        marketingService.updateMkItemNoFile(mkItem);

        if(file.getSize() > 0) {
            originFileName = file.getOriginalFilename();
            ext = originFileName.substring(originFileName.lastIndexOf("."));
            changeName = UUID.randomUUID().toString().replace("-", "");

            mkItem.setItemOriName(originFileName);
            mkItem.setItemFileName(changeName + ext);

            marketingService.updateMkItem(mkItem);

            try {
                file.transferTo(new File(filePath + mkdir.separator + changeName + ext));
            } catch (IOException e) {

                e.printStackTrace();
                new File(filePath + mkdir.separator + changeName + ext).delete();
            }
        }

        rttr.addFlashAttribute("message", "수정이 완료되었습니다.");
        rttr.addFlashAttribute("check", "success");

        return "redirect:/marketing/detail/modify?mkNum=" + mkNum;
    }

    @GetMapping("/detail/delete/cast")
    public String marketingDeletePoster(@ModelAttribute MkPosterDTO mkPoster, RedirectAttributes rttr) throws Exception{

        int mkNum = mkPoster.getMkNum();
        int pFileNum = mkPoster.getpFileNum();

        marketingService.deleteMkPoster(pFileNum);

        rttr.addFlashAttribute("message", "삭제가 완료되었습니다.");
        rttr.addFlashAttribute("check", "success");

        return "redirect:/marketing/detail/modify?mkNum=" + mkNum;
    }

    @GetMapping("/detail/delete/md")
    public String marketingDeleteMd(@ModelAttribute MkMdDTO mkMd, RedirectAttributes rttr) throws Exception{

        int mkNum = mkMd.getMkNum();
        int mdFileNum = mkMd.getMdFileNum();

        marketingService.deleteMkMd(mdFileNum);

        rttr.addFlashAttribute("message", "삭제가 완료되었습니다.");
        rttr.addFlashAttribute("check", "success");

        return "redirect:/marketing/detail/modify?mkNum=" + mkNum;
    }

    @GetMapping("/detail/delete/item")
    public String marketingDeleteItem(@ModelAttribute MkItemDTO mkItem, RedirectAttributes rttr) throws Exception{

        int mkNum = mkItem.getMkNum();
        int itemFileNum = mkItem.getItemFileNum();

        marketingService.deleteMkItem(itemFileNum);

        rttr.addFlashAttribute("message", "삭제가 완료되었습니다.");
        rttr.addFlashAttribute("check", "success");

        return "redirect:/marketing/detail/modify?mkNum=" + mkNum;
    }
}
