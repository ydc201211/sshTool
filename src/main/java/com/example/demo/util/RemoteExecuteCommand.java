package com.example.demo.util;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;

/**
 * @author ydc 2019/3/19 13:52
 */
public class RemoteExecuteCommand {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RemoteExecuteCommand.class);
    //字符编码默认是utf-8
    private static String defaultChart = "UTF-8";
    private static Connection conn;
    private static String ip;
    private static String userName;
    private static String userPwd;

    public RemoteExecuteCommand(String ip, String userName, String userPwd,String defaultChart) {
        this.ip = ip;
        this.userName = userName;
        this.userPwd = userPwd;
        this.defaultChart = defaultChart;
    }

    /**
     * @Description 远程登录linux的主机
     * @return 登录成功返回true，否则返回false
     */
    private static Boolean login() {
        boolean flg = false;
        try {
            conn = new Connection(ip);
            conn.connect();//连接
            flg = conn.authenticateWithPassword(userName, userPwd);//认证
            if (flg) {
                LOGGER.info("认证成功！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flg;
    }

    /**
     * @Description 远程执行shll脚本或者命令
     * @param cmd 即将执行的命令
     * @return 命令执行完后返回的结果值
     */
    private static String execute(String cmd) {
        String result = "";
        Session session = null;
        try {
            if (login()) {
                LOGGER.info("=====打开会话=====");
                session = conn.openSession();//打开一个会话
                session.execCommand(cmd);//执行命令
                result = processStdout(session.getStdout(), defaultChart);
                //如果为得到标准输出为空，说明脚本执行出错了
                if (StringUtils.isEmpty(result)) {
                    LOGGER.info("脚本出错");
                    result = processStdout(session.getStderr(), defaultChart);
                }
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            conn.close();
            session.close();
        }
        return result;
    }


    /**
     * @Description 远程执行shll脚本或者命令
     * @param cmd 即将执行的命令
     * @return 命令执行成功后返回的结果值，如果命令执行失败，返回空字符串，不是null
     *
     */
    public String executeSuccess(String cmd) {
        String result = "";
        Session session = null;
        try {
            if (login()) {
                LOGGER.info("=====打开会话=====");
                session = conn.openSession();//打开一个会话
                session.execCommand(cmd);//执行命令
                result = processStdout(session.getStdout(), defaultChart);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            conn.close();
            session.close();
        }
        return result;
    }

    /**
     * @Description 解析脚本执行返回的结果集
     * @param in      输入流对象
     * @param charset 编码
     * @return 以纯文本的格式返回
     */
    public static String processStdout(InputStream in, String charset) {
        InputStream stdout = new StreamGobbler(in);
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line + "\n");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    /**
     * @Description 发送指令
     * @author ydc
     * @param ip
     * @param username
     * @param password
     * @param cmd
     * @param defaultChart
     * @return 解析结果
     */
    public static String sendCommand(String ip,String username,String password,
                                     String cmd,String defaultChart){
        RemoteExecuteCommand rec=new RemoteExecuteCommand(ip, username,password,defaultChart);
        return execute(cmd);
    }

}
