
package swing.demon.util.props;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Props {
	Map<String, Object> props = new HashMap();
    List<Object[]> propList = new ArrayList<Object[]>();

    public Props(String path) throws PropsException, IOException {
        File propsFile = this.openProps(path);
        this.readProps(propsFile);
    }

    File openProps(String path) throws PropsException {
        File ret = new File(path);
        if (!ret.exists()) {
            throw new PropsException("설정 파일이 없습니다.");
        } else if (!ret.canRead()) {
            throw new PropsException("설정 파일을 읽을 수 없습니다.");
        } else {
            return ret;
        }
    }

    void readProps(File file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line = null;

        while((line = br.readLine()) != null) {
            if (!line.isEmpty()) {
                if(line.substring(0, 1).equals("#")) {
                    continue;
                }
                String[] temp = line.split("=");
                propList.add(new Object[] {temp[0].trim(), temp[1].trim()});
                this.props.put(temp[0].trim(), temp[1].trim());
            }
        }

    }

	public List<Object[]> getPropList() {
		return propList;
	}
    
	public Object get(String key) {
        return this.props.get(key);
    }

    public int getInt(String key) {
        return Integer.parseInt((String)this.props.get(key));
    }

    public String getString(String key) {
        return (String)this.props.get(key);
    }

    public boolean getBoolean(String key) {
        if(this.props.get(key) == null) {
            return false;
        }
        return Boolean.valueOf((String)this.props.get(key));
    }
}
