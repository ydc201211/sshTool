package com.example.demo.controller;/**
 * Created by ydc on 2019/3/12.
 */

import com.example.demo.util.RemoteExecuteCommand;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ydc 2019/3/12 11:28
 */
@RestController
@RequestMapping("/app")
public class TestController {

    @ApiOperation(value="发送远程调用命令", notes="发送linux命令")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ip", value = "远程连接IP", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "username", value = "用户名", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "密码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "cmd", value = "linux命令", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "charset", value = "字符编码", paramType = "query", dataType = "string")
    })

    @RequestMapping(value = "/test",method =  { RequestMethod.POST})
    public String test(String ip,String username,String password,String cmd,String charset){
        String ret = RemoteExecuteCommand.sendCommand(ip,username,
                password,cmd,charset);
        return ret;
    }
}
