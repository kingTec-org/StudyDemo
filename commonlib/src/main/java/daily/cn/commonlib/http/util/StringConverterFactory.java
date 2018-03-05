package daily.cn.commonlib.http.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * author :zuoshengyong
 * e-mail: partric23@gmail.com
 * time: 2018/03/05
 */

public class StringConverterFactory extends Converter.Factory{
    public static StringConverterFactory create() {
        return new StringConverterFactory();
    }

    private final class ConfigurationServiceConverter implements Converter<ResponseBody, String> {
        @Override
        public String convert(ResponseBody value) throws IOException {
            BufferedReader r = new BufferedReader(new InputStreamReader(value.byteStream()));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            return total.toString();
        }
    }
}
