package com.goldenage.project.space.controller;

import com.goldenage.project.exception.notice.NoticeDeleteException;
import com.goldenage.project.exception.space.SpaceDeleteException;
import com.goldenage.project.exception.space.SpaceUpdateException;
import com.goldenage.project.notice.model.dto.NoticeDTO;
import com.goldenage.project.space.model.dto.SpaceDTO;
import com.goldenage.project.space.model.dto.SpacePhoDTO;
import com.goldenage.project.space.model.service.SpaceServiceImpl;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/space/*")
public class SpaceController {

    private final SpaceServiceImpl spaceService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public SpaceController(SpaceServiceImpl spaceService) {
        this.spaceService = spaceService;
    }

    // 관리자 연습실 리스트 출력
    @GetMapping("/spaceList")
    public ModelAndView spaceList(ModelAndView mv, HttpServletRequest request){

        List<SpaceDTO> spaceList = spaceService.selectSpaceList();

        log.info("뭘까정말" + spaceList);

        mv.addObject("spaceList", spaceList);
        mv.setViewName("space/spaceList");

        return mv;

    }

    // 관리자 연습실 추가 화면보기
    @GetMapping("/spaceInsert")
    public String spaceInsertView(){

        return "space/spaceInsert";
    }

    //관리자 연습실 추가하기
    @PostMapping("/spaceInsert")
    public String insertSpace(@ModelAttribute SpaceDTO space
            , @RequestParam(value="multiFiles", required = false) List<MultipartFile> multiFiles
            , RedirectAttributes rttr) throws Exception {

        SpacePhoDTO spacePho = new SpacePhoDTO();

        int result = spaceService.insertSpace(space);

        if(result> 0){

            space.setSpaceNum(spaceService.selectNum());

            log.info("num 을 알려줘 " + space.getSpaceNum());
        }

        String root = ResourceUtils.getURL("upload").getPath();

        String filePath = root + "/space";

        File mkdir = new File(filePath);
        if(!mkdir.exists()) {
            mkdir.mkdirs();
        }

        if(multiFiles.get(0).isEmpty() == false){
            Map<String,String> files = new HashMap<>();
            for(int i = 0; i < multiFiles.size(); i++) {

                String spaceOriName = "";
                String ext = "";
                String spaceFileName = "";
                int spaceNum = 0;

                spaceOriName = multiFiles.get(i).getOriginalFilename();
                ext = spaceOriName.substring(spaceOriName.lastIndexOf("."));
                spaceFileName = UUID.randomUUID().toString().replace("-", "") + ext;

                spaceNum = space.getSpaceNum();

                files.put("spaceOriName", spaceOriName);
                files.put("spaceFileName", spaceFileName);
                files.put("spaceNum", String.valueOf(spaceNum));

                spaceService.insertSpacePho(files);

                try {

                    multiFiles.get(i).transferTo(new File(filePath + mkdir.separator + files.get("spaceFileName")));

                } catch (IOException e) {

                    new File(filePath + mkdir.separator + files.get("spaceFileName")).delete();

                }
            }

        }else if(multiFiles.get(0).isEmpty() == true){
            Map<String,String> files = new HashMap<>();

            String spaceOriName = "";
            String spaceFileName = "";
            int spaceNum = 0;

            spaceNum = space.getSpaceNum();

            files.put("spaceOriName", spaceOriName);
            files.put("spaceFileName", spaceFileName);
            files.put("spaceNum", String.valueOf(spaceNum));

            spaceService.insertSpacePho(files);

        }
        rttr.addFlashAttribute("message", "연습실 등록 성공!");

        return "redirect:/space/spaceList";

    }

    //연습실 삭제
    @GetMapping("/delete")
    public String spaceDelete(HttpServletRequest request, RedirectAttributes rttr) throws SpaceDeleteException, FileNotFoundException {

        int spaceNum = Integer.parseInt(request.getParameter("spaceNum"));

        List<SpacePhoDTO> phoList = spaceService.selectPho(spaceNum);
        int result = spaceService.deleteSpace(spaceNum);

        if(result > 0) {

            // 연습실 이미지 삭제
            String root = ResourceUtils.getURL("upload").getPath();

            String filePath = root + "/space";

            for(int i = 0; i < phoList.size(); i++){

                File mkdirCast = new File(filePath + File.separator + phoList.get(i).getSpaceFileName());

                if(mkdirCast.exists()) {

                    mkdirCast.delete();
                }
            }
        }

        rttr.addFlashAttribute("message", "연습실 삭제 성공!");

        return "redirect:/space/spaceList";
    }

    //사용자 연습실 뷰 화면
    @GetMapping("/theater")
    public ModelAndView theaher(ModelAndView mv){

        List<SpaceDTO> spaceList = spaceService.selectSpaceListView();

        if(spaceList.isEmpty()){

            mv.setViewName("space/theater");
        } else {

            int spaceNum = spaceList.get(0).getSpaceNum();

            List<SpacePhoDTO> phoList = spaceService.selectPho(spaceNum);
            SpaceDTO spaceView = spaceService.selectSpaceView(spaceNum);

            mv.addObject("spaceList", spaceList);
            mv.addObject("spacePhoList", phoList);
            mv.addObject("spaceView", spaceView);
            mv.setViewName("space/theater");
        }
        return mv;
    }

    // 리스트 선택 후
    @GetMapping("/theater/number")
    public ModelAndView theaherNum(ModelAndView mv, HttpServletRequest request){

        List<SpaceDTO> spaceList = spaceService.selectSpaceListView();

        int spaceNum = Integer.parseInt(request.getParameter("spaceNum"));

        log.info(spaceNum + "뭘가여");

        List<SpacePhoDTO> phoList = spaceService.selectPho(spaceNum);
        SpaceDTO spaceView = spaceService.selectSpaceView(spaceNum);

        mv.addObject("spaceList", spaceList);
        mv.addObject("spacePhoList", phoList);
        mv.addObject("spaceView", spaceView);
        mv.setViewName("space/theater");

        return mv;
    }


    //관리자 연습실 내용 수정하기
    @GetMapping("/spaceUpdate")
    public ModelAndView spaceUpDate(ModelAndView mv, HttpServletRequest request){

        int spaceNum = Integer.parseInt(request.getParameter("spaceNum"));

        SpaceDTO spaceIntro = spaceService.selectSpaceIntro(spaceNum);
        log.info("뭐지이 " + spaceNum);

        mv.addObject("spaceIntro", spaceIntro);
        mv.setViewName("space/spaceUpdate");

        return mv;
    }

    //연습실 내용 수정
    @PostMapping("/spaceUpdate")
    public String spaceUpdate(@ModelAttribute SpaceDTO space,
                              RedirectAttributes rttr)throws SpaceUpdateException{

        int result = spaceService.updateSpace(space);

        if(result > 0){
            rttr.addFlashAttribute("message", "연습실 수정 성공!");
        }

        return "redirect:/space/spaceList";
    }

    @GetMapping("/spacePhoUpdate")
    public ModelAndView spacePhoUpDate(ModelAndView mv,HttpServletRequest request){

        int spaceNum = Integer.parseInt(request.getParameter("spaceNum"));

        List<SpacePhoDTO> spacePhoList = spaceService.selectSpacePho(spaceNum);
        log.info("spaceNum??" + spaceNum);

        mv.addObject("spacePhoList", spacePhoList);
        mv.addObject("spaceNum",spaceNum);
        mv.setViewName("space/spacePhoUpdate");

        return mv;
    }

    //연습실 사진 한개 삭제
    @GetMapping("/space/pho/delete")
    public String spacePhoDelete(@ModelAttribute SpacePhoDTO spacePho,
                                 @RequestParam(value="spaceFileNum", required = false) int spaceFileNum,
                                 @RequestParam(value="spaceNum", required = false) int spaceNum) throws FileNotFoundException {

        SpacePhoDTO photo = spaceService.selectPhoto(spaceFileNum);
        int result = spaceService.deleteSpacePho(spaceFileNum);

        String root = ResourceUtils.getURL("upload").getPath();

        String filePath = root + "/space";

        if(result > 0) {
            File fileDel = new File(filePath + File.separator + photo.getSpaceFileName());

            if (fileDel.exists()) {

                fileDel.delete();
            }
        }

        return "redirect:/space/spacePhoUpdate?spaceNum=" + spaceNum;
    }

    @PostMapping("/space/pho/insert")
    public String spacePhoInsert(@ModelAttribute SpacePhoDTO spacePho,
                                 @RequestParam(value="file", required=false) MultipartFile file,
                                 @RequestParam(value="spaceNum", required = false) int spaceNum) throws FileNotFoundException {

        SpacePhoDTO spacePhoto = new SpacePhoDTO();

        String root = ResourceUtils.getURL("upload").getPath();

        String filePath = root + "/space";

        File mkdir = new File(filePath);
        if(!mkdir.exists()) {
            mkdir.mkdirs();
        }
        String spaceOriName = "";
        String ext = "";
        String spaceFileName = "";

        if(file.getSize() > 0){
            spaceOriName = file.getOriginalFilename();
            ext = spaceOriName.substring(spaceOriName.lastIndexOf("."));
            spaceFileName = UUID.randomUUID().toString().replace("-", "") + ext;

            spacePhoto.setSpaceOriName(spaceOriName);
            spacePhoto.setSpaceFileName(spaceFileName + ext);
            spacePhoto.setSpaceNum(spaceNum);

            spaceService.insertSpacePhoto(spacePhoto);

            try {

                file.transferTo(new File(filePath + mkdir.separator + spaceFileName + ext));
            } catch (IOException e) {

                e.printStackTrace();
                new File(filePath + mkdir.separator + spaceFileName + ext).delete();
            }
        }

        return "redirect:/space/spacePhoUpdate?spaceNum=" + spaceNum;
    }
}
