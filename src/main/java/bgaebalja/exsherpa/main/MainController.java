package bgaebalja.exsherpa.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
    @GetMapping("/main.do")
    public String getMainPage() {
        return "main";
    }
}