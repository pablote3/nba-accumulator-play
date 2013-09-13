package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import play.libs.F;
import play.libs.F.Option;
import play.mvc.QueryStringBindable;

public class DateW implements QueryStringBindable<DateW> {
    public Date value = null;

    @Override
    public Option<DateW> bind(String key, Map<String, String[]> data) {
        String[] vs = data.get(key);
        if (vs != null && vs.length > 0) {
            String v = vs[0];
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
            try {
				value = sdf.parse(v);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            //return value of type Some<T> containing value built from query string data
            return F.Some(this);
        }
        //return none when no parameter key in query string
        return F.None();
    }

    @Override
    public String unbind(String key) {
        return key + "=" + value;
    }

    @Override
    public String javascriptUnbind() {
         return value.toString();
	}
}
