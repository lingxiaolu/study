package com.zwq.controller;

import com.zwq.dao.ResumeDao;
import com.zwq.pojo.Resume;
import com.zwq.service.IResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
@Controller
@RequestMapping("/resume")
public class ResumeController {

    @Autowired
    private IResumeService resumeService;

    @RequestMapping("/index")
    public ModelAndView queryResumeList(ModelAndView modelAndView){
        List<Resume> optionalList = resumeService.queryResumeList();
        modelAndView.addObject("list", optionalList);
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @RequestMapping(value = "/resume",method = {RequestMethod.PUT})
    public void updateResume(HttpServletRequest request, HttpServletResponse response,Resume resume, ModelAndView modelAndView) throws IOException {
        resumeService.updateResume(resume);
        response.sendRedirect("index");
    }

    @RequestMapping(value = "/resume",method = {RequestMethod.GET})
    public ModelAndView toUpdateResume(Resume resume,ModelAndView modelAndView){
        Example example = Example.of(resume);
        Optional optional = resumeService.toUpdateResume(example);
        modelAndView.addObject("resume", optional.get());
        modelAndView.setViewName("update");
        return modelAndView;
    }
    @RequestMapping(value = "/resume",method = {RequestMethod.DELETE})
    public void removeResume(HttpServletRequest request, HttpServletResponse response,Resume resume,ModelAndView modelAndView) throws IOException {
        resumeService.removeResume(resume);
        response.sendRedirect("index");
    }
    @RequestMapping("/insert")
    public String toInsertResume(){
        return "insert";
    }

    @RequestMapping(value = "/resume",method = {RequestMethod.POST})
    public void insertResume(HttpServletRequest request, HttpServletResponse response,Resume resume,ModelAndView modelAndView) throws IOException {
        Resume save = resumeService.insertResume(resume);
        response.sendRedirect("index");
    }

}
