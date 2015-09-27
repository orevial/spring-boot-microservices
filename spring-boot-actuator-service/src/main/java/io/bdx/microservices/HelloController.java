package io.bdx.microservices;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Olivier on 27/09/2015.
 */
@Controller
public class HelloController {

    @ResponseBody
    @RequestMapping("/hello")
    public String helloBdxIo() {
        return "Hello BDX I/O !";
    }
}
