package com.project.back_end.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DashboardController {


  @Autowire
  private Service service;

  @GetMapping(/adminDashboard/{token})
  public String adminDashboard(@PathVariable("token") String token) {

    //Validate the token for the 'admin' role
    boolean isValid = service.validateToken(token, "admin")

      if (isValid) {
        // Forward to admin dashboard
        return "admin/adminDashboard";
      } else {
        //Redirect to root (login/home page)
        return "redirect:http://localhost:8080";
      }
      
  }

  @GetMapping(/doctorDashboard/{token})
  public String doctorDashboard(@PathVariable("token") String token) {

    //Validate the token for the 'admin' role
    boolean isValid = service.validateToken(token, "doctor")

      if (isValid) {
        // Forward to doctor dashboard
        return "doctor/doctorDashboard";
      } else {
        //Redirect to root (login/home page)
        return "redirect:http://localhost:8080";
      }
      
  }




}
