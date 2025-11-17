package com.example.backend.service.student.lc_autonomous_learn;

import com.example.backend.controller.student.dto.StudentLCAutonomousLearnListDto;
import com.example.backend.entity.MediaAssets;

import java.util.List;

public interface StudentLCALVideosService {
    List<MediaAssets> getVideosList(StudentLCAutonomousLearnListDto req);
    Long getVideosCount(StudentLCAutonomousLearnListDto req);
}