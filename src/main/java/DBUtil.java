import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.security.PrivateKey;

//封装DataSource的单例
public class DBUtil {
    private static volatile DataSource dataSource=null;

    public static DataSource getDataSource() {

        if (dataSource==null) {
            synchronized (DBUtil.class) {
                if (dataSource == null) {
                    dataSource = new MysqlDataSource();
                    ((MysqlDataSource) dataSource).setURL("jdbc:mysql://127.0.0.1:3306/messageWall?characterEncoding=utf8&useSSL=false");
                    ((MysqlDataSource) dataSource).setUser("root");
                    ((MysqlDataSource) dataSource).setPassword("123456");
                }
            }
        }
        return dataSource;
    }
    private DBUtil(){

    }
}
