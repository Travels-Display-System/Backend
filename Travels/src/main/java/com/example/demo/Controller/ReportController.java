package com.example.demo.Controller;

import com.example.demo.Entity.Report;
import com.example.demo.Service.ReportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping(value = "report/query")
    public List<Report> getReport(@RequestParam(value = "travelid",required = false) Long travelid) throws JsonProcessingException {
        return reportService.getReport(travelid);
    }

    @PostMapping(value = "report")
    public Report createReport(@RequestBody  Report report) throws JsonProcessingException {
        return reportService.createReport(report);
    }

    @PostMapping(value = "report/noreport")
    public void deleteReport(@RequestBody  Report report) throws JsonProcessingException {
        reportService.deleteReport(report);
    }
}
