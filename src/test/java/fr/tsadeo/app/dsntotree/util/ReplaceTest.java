package fr.tsadeo.app.dsntotree.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class ReplaceTest {

    public static void main(String[] args) {

        new ReplaceTest().testReplaceInPattern();
    }

    @Test
    public void testReplaceInPattern() {

        String text = "jkfdqfjsd <b> fjfjqskdjl <b> ueorausdi";
        Pattern pattern = Pattern.compile("<b>");

        StringBuffer sb = new StringBuffer();
        Matcher m = pattern.matcher(text);
        while (m.find()) {
            m.appendReplacement(sb, Matcher.quoteReplacement("toto"));
        }
        m.appendTail(sb);

        System.out.println(sb.toString());
    }

    @Test
    public void testReplaceSql() {

        String text = "INSERT INTO DSRUBRIQUE(DSRUIDFSYS,DSRUNORME,DSRUCODRUB,DSRULIBRUB,DSRUBLCRAT,DSRULIBBLC,DSRUNATRUB,DSRUMINRUB,DSRUMAXRUB,DSRUREFVAL) ;VALUES ('398','DSN','11.019','Date d''entrée dans le TESE CEA','11','Etablissement','D','','','11.019');";

        Pattern pattern = Pattern.compile(
                "(INSERT INTO[\\w\\s]*\\([\\w\\s,']*DSRUMAXRUB,DSRUREFVAL\\)[\\w\\s,'\\S]*)(,'','','[\\d]{2}\\.[\\d]{3}'\\);$)");

        Matcher m = pattern.matcher(text);
        if (m.matches()) {
            System.out.println("OK");
            int count = m.groupCount();
            if (count == 2) {

                String prefix = m.group(1);
                String suffix = m.group(2);

                System.out.println("prefix: " + prefix);
                System.out.println("suffix: " + suffix);

                suffix = suffix.replaceAll("''", "null");

                String sql = prefix + suffix;
                System.out.println("sql: " + sql);
            }

        }

    }

}
