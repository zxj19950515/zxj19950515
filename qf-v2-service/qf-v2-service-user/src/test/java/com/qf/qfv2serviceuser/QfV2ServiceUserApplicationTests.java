package com.qf.qfv2serviceuser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QfV2ServiceUserApplicationTests {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Test
    public void contextLoads() {


    }

    @Test
    public void testBCryptPasswordEncoder(){
//        System.out.println("-----> "+bCryptPasswordEncoder.encode("123"));
        System.out.println("----> "+bCryptPasswordEncoder.matches("123", "$2a$10$7FdcxMW/B6hbaCUV8lqwL.oLTsvzF.0ue4znEC595Xr30L88CiUOi"));
        System.out.println("----> "+bCryptPasswordEncoder.matches("123", "$2a$10$HTHwvvewfphe/pZnWDwpG.D/gtXGRay77I9Zo5a3dWvS3c40yOAVe"));

    }

}
