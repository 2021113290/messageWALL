import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.jdbc.MysqlDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/message")
public class MessageServlet extends HttpServlet {
//    该对象在多个方法中都需要使用
    private ObjectMapper objectMapper=new ObjectMapper();
//    private List<Message> messageList=new ArrayList<>();
    //    负责让页面获取数据
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=utf8");
//        把messageList转成json字符串并返回给页面
        List<Message> messageList= null;
        try {
            messageList = load();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        resp.getWriter().write(objectMapper.writeValueAsString(messageList));
    }
//    提交数据

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        获取body中的数据并解析
        Message message=objectMapper.readValue(req.getInputStream(),Message.class);
//        把这个message保存在内存中
//        messageList.add(message);
        try {
            save(message);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        resp.setStatus(200);
        System.out.println("提交数据成功：from="+ message.getFrom()+",to="+message.getTo()+",message="+message.getMessage());
    }
    private List<Message> load() throws SQLException {
        //从数据库查询数据
        //        1.先有一个数据源
        DataSource dataSource=DBUtil.getDataSource();
//        2.建立连接
        Connection connection=dataSource.getConnection();
//        3.构造sql
        String sql="select * from message";
        PreparedStatement statement=connection.prepareStatement(sql);
//        4.执行sql
        ResultSet resultSet=statement.executeQuery();
//        5.遍历结果集合
        List<Message> messageList =new ArrayList<>();
        while (resultSet.next()){
            Message message=new Message();
            message.setFrom(resultSet.getString("from"));
            message.setTo(resultSet.getString("to"));
            message.setMessage(resultSet.getString("message"));
            messageList.add(message);
        }
//         6.关闭连接
        statement.close();
        connection.close();
        return messageList;
    }
    private void save(Message message) throws SQLException {
        //把数据保存到数据库中
//        1.先有一个数据源
        DataSource dataSource=DBUtil.getDataSource();
//        2.建立连接
        Connection connection=dataSource.getConnection();
//        3.构造sql
        String sql="insert into message value(?,?,?)";
        PreparedStatement statement=connection.prepareStatement(sql);
        statement.setString(1,message.getFrom());
        statement.setString(2,message.getTo());
        statement.setString(3,message.getMessage());
//        4.执行sql
        int ret= statement.executeUpdate();
        System.out.println("ret= "+ret);
//5.关闭连接
        statement.close();
        connection.close();
    }
}
