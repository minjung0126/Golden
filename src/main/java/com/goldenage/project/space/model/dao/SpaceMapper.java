package com.goldenage.project.space.model.dao;

import com.goldenage.project.space.model.dto.SpaceDTO;
import com.goldenage.project.space.model.dto.SpacePhoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SpaceMapper {
    List<SpaceDTO> selectSpaceList();

    int insertSpace(SpaceDTO space);

    int deleteSpaceFile(int spaceNum);

    int deleteSpace(int spaceNum);

    int insertSpacePhoto(Map<String, String> pho);

    int selectNum();

    List<SpaceDTO> selectSpaceListView();

    List<SpacePhoDTO> selectPho(int spaceNum);
    SpaceDTO selectSpaceView(int spaceNum);

    SpaceDTO selectSpaceIntro(int spaceNum);

    int updateSpace(SpaceDTO space);

    List<SpacePhoDTO> selectSpacePho(int spaceNum);

    int deleteSpacePho(int spaceFileNum);

    int insertSpacePho(SpacePhoDTO spacePhoto);


}
