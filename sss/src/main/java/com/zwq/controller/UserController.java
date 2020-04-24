package com.zwq.controller;

import com.zwq.dao.ResumeDao;
import com.zwq.dao.UserDao;
import com.zwq.pojo.Resume;
import com.zwq.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserDao userDao;
    @RequestMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response,User user) throws IOException {
        if (user.getUsername()!=null && user.getPassword()!=null){
            /*User user1 = userDao.findBySql(user.getUsername(), user.getPassword());
            if (user1!=null){
                HttpSession session = request.getSession(true);
                session.putValue("login",true);
                response.sendRedirect("/resume/index");
            }*/
            if("admin".equals(user.getUsername())&&"admin".equals(user.getPassword())){
                HttpSession session = request.getSession(true);
                session.putValue("login",true);
                response.sendRedirect("/resume/index");
            }
        }
        return "error";
    }

    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }
}
