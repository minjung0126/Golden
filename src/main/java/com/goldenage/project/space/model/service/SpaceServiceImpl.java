package com.goldenage.project.space.model.service;

import com.goldenage.project.exception.space.SpaceDeleteException;
import com.goldenage.project.exception.space.SpaceInsertException;
import com.goldenage.project.space.model.dao.SpaceMapper;
import com.goldenage.project.space.model.dto.SpaceDTO;
import com.goldenage.project.space.model.dto.SpacePhoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service("spaceService")
public class SpaceServiceImpl implements SpaceService{

    private SpaceMapper spaceMapper;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public SpaceServiceImpl(SpaceMapper spaceMapper) {
        this.spaceMapper = spaceMapper;

    }

    // 관리자 연습실 리스트
    @Override
    public List<SpaceDTO> selectSpaceList() {

        List<SpaceDTO> spaceList = spaceMapper.selectSpaceList();

        return spaceList;
    }
    // 연습실 등록
    @Override
    @Transactional
    public int insertSpace(SpaceDTO space) throws SpaceInsertException {

        int result = spaceMapper.insertSpace(space);


        if(!(result > 0)){

            throw new SpaceInsertException("연습실 등록 실패!");

        }

        return result;
    }

    // 연습실 사진 등록
    @Override
    @Transactional
    public int insertSpacePho(Map<String, String> files) {

        int result = spaceMapper.insertSpacePhoto(files);

        return result;
    }


    //연습실 삭제
    @Override
    @Transactional
    public int deleteSpace(int spaceNum) throws SpaceDeleteException {

        int result = spaceMapper.deleteSpaceFile(spaceNum);
        int result2 = spaceMapper.deleteSpace(spaceNum);

        if(!(result2 > 0)){

            throw new SpaceDeleteException("연습실 삭제 실패!");
        }

        return result;
    }

    //연습실 추가시 연습실 번호찾기
    @Override
    public int selectNum() {

        int selectNum = spaceMapper.selectNum();

        return selectNum;
    }

    //사용자 연습실 리스트 탭
    @Override
    public List<SpaceDTO> selectSpaceListView() {

        List<SpaceDTO> spaceList = spaceMapper.selectSpaceListView();

        return spaceList;
    }

    //사용자 연습실뷰 사진리스트
    @Override
    public List<SpacePhoDTO> selectPho(int spaceNum) {

        List<SpacePhoDTO> phoList = spaceMapper.selectPho(spaceNum);

        return phoList;
    }

    //연슶실 내용정보
    @Override
    public SpaceDTO selectSpaceView(int spaceNum) {

        SpaceDTO spaceView = spaceMapper.selectSpaceView(spaceNum);

        return spaceView;
    }

    //연습실 내용 수정
    @Override
    public SpaceDTO selectSpaceIntro(int spaceNum) {

        SpaceDTO spaceIntro = spaceMapper.selectSpaceIntro(spaceNum);

        return spaceIntro;
    }

    //연습실 내용 수정
    @Override
    @Transactional
    public int updateSpace(SpaceDTO space) {

        int result = spaceMapper.updateSpace(space);

        return result;
    }

    @Override
    public List<SpacePhoDTO> selectSpacePho(int spaceNum) {

        List<SpacePhoDTO> selectSpacePho = spaceMapper.selectSpacePho(spaceNum);

        return selectSpacePho;

    }
    @Override
    public int deleteSpacePho(int spaceFileNum) {

        int result = spaceMapper.deleteSpacePho(spaceFileNum);

        return result;
    }

    @Override
    public int insertSpacePhoto(SpacePhoDTO spacePhoto) {

        int result = spaceMapper.insertSpacePho(spacePhoto);

        return result;
    }

    @Override
    public SpacePhoDTO selectPhoto(int spaceFileNum) {

        SpacePhoDTO selectSpacePhoto = spaceMapper.selectSpacePhoto(spaceFileNum);

        return selectSpacePhoto;
    }
}

