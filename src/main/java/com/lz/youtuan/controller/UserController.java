package com.lz.youtuan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lz.youtuan.common.R;
import com.lz.youtuan.entity.User;
import com.lz.youtuan.service.UserService;
import com.lz.youtuan.utils.SMSUtils;
import com.lz.youtuan.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone=user.getPhone();

        if(StringUtils.isNotBlank(phone)){
            //生成随机验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);  //没调用阿里云服务，控制台输出验证码登录

            //调用阿里云的短信服务API完成发送短信
            //SMSUtils.sendMessage("阿里云签名","自己建立的模板",phone,code);  要花钱

            //将生成的验证码存到Session
            //session.setAttribute(phone,code);

            //将生成的code缓存到Redis中，设置时间为5分钟
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);

            return R.success("手机验证码发送成功");
        }
        return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        //获取手机号
        String phone=map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从Session中获得保存的验证码
        //Object codeSession = session.getAttribute(phone);

        //从Redis中获取缓存验证码
        Object codeSession = redisTemplate.opsForValue().get(phone);

        //进行验证码对比
        if(codeSession!=null && codeSession.equals(code)){
            //对比成功 登录成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);

            User user = userService.getOne(queryWrapper);
            if(user==null){
                //判断是否为新用户 新用户则自动完成注册
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());

            //如果用户登录成功 删除Redis中缓存验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }
        return R.error("登录失败");
    }

}
