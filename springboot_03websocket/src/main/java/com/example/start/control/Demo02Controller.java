package com.example.start.control;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Random;

/**
 * @description:
 * @createDate: 2020/12/15
 * @author:
 */
@RestController
@RequestMapping("/demo")
public class Demo02Controller {

  @GetMapping("/index")
  public ModelAndView index(){
    ModelAndView mav=new ModelAndView("socket");
    Random random =new Random(100000);
    int uid = random.nextInt();
    mav.addObject("uid",uid);
    return mav;
  }

}

