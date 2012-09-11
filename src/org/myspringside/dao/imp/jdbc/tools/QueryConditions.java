package org.myspringside.dao.imp.jdbc.tools;

import java.util.regex.Pattern;

/**
 *@author Alex
 */
public interface QueryConditions {
	Pattern pat = Pattern.compile("\\S*[^><]=\\S+", Pattern.CASE_INSENSITIVE);
	Pattern pat2 = Pattern.compile("\\S+ like \\S+", Pattern.CASE_INSENSITIVE);
	Pattern pat3 = Pattern.compile("\\S+ asc", Pattern.CASE_INSENSITIVE);
	Pattern pat4 = Pattern.compile("\\S+ desc", Pattern.CASE_INSENSITIVE);
	Pattern pat5 = Pattern.compile("\\S+>[^=]\\S*", Pattern.CASE_INSENSITIVE);
	Pattern pat6 = Pattern.compile("\\S+<[^=]\\S*", Pattern.CASE_INSENSITIVE);
	Pattern pat7 = Pattern.compile("\\S+>=\\S+", Pattern.CASE_INSENSITIVE);
	Pattern pat8 = Pattern.compile("\\S+<=\\S+", Pattern.CASE_INSENSITIVE);
	Pattern pat9 = Pattern.compile("\\S+ between \\S+ and \\S+",
			Pattern.CASE_INSENSITIVE);
	Pattern pat10 = Pattern.compile("'\\S+'", Pattern.CASE_INSENSITIVE);
}
