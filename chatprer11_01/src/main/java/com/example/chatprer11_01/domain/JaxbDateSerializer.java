package com.example.chatprer11_01.domain;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JaxbDateSerializer extends XmlAdapter<String, Date> {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    public Date unmarshal(String s) throws Exception {
        return simpleDateFormat.parse(s);
    }

    @Override
    public String marshal(Date date) throws Exception {
        return simpleDateFormat.format(date);
    }
}
