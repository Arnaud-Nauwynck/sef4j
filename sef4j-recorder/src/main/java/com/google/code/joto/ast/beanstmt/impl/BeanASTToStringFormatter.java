package com.google.code.joto.ast.beanstmt.impl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.google.code.joto.ast.beanstmt.BeanAST;
import com.google.code.joto.util.ToStringFormatter;

public class BeanASTToStringFormatter implements ToStringFormatter<BeanAST> {

	private static final BeanASTToStringFormatter instance = new BeanASTToStringFormatter();
	public static BeanASTToStringFormatter getInstance() {
		return instance;
	}

	public String objectToString(BeanAST p) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		BeanASTPrettyPrinter printer = new BeanASTPrettyPrinter(new PrintStream(buffer));
		p.visit(printer);
		String res = buffer.toString(); 
		res = res.trim();
		if (res.endsWith("\n")) {
			res = res.substring(0, res.length() - 1);
		}
		return res;
	}


}
