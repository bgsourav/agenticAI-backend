package com.example.kitt.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("username", authentication.getName());
            return "dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/job1")
    public String job1(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("jobName", "Job 1");
            return "job-details";
        }
        return "redirect:/login";
    }

    @GetMapping("/job2")
    public String job2(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("jobName", "Job 2");
            return "job-details";
        }
        return "redirect:/login";
    }

    @GetMapping("/job3")
    public String job3(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("jobName", "Job 3");
            return "job-details";
        }
        return "redirect:/login";
    }
}