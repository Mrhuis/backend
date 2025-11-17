package com.example.backend.service.student.lc_autonomous_learn;

import com.example.backend.controller.student.dto.StudentLCAutonomousLearnListDto;
import com.example.backend.entity.Item;

import java.util.List;

public interface StudentLCALItemsService {
    List<Item> getItemsList(StudentLCAutonomousLearnListDto req);
    Long getItemsCount(StudentLCAutonomousLearnListDto req);
}