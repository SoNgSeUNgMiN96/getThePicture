package org.tensorflow.lite.examples.detection.Dictionary;

import java.util.HashMap;

public class RabelDic {
    static HashMap<String,String> labelDic = new HashMap<String,String>(){
        {
            put("컵","cup");     //mouse
            put("마우스","mouse");     //keyboard
            put("키보드","keyboard");
            put("휴대폰","cell phone");
            put("스마트폰","cell phone");
            put("폰","cell phone");
            put("시계","clock");
            put("책","book");
            put("키보드","keyboard");
            put("사람","person");     //mouse
            put("인간","person");     //mouse
            put("개","dog");
            put("강아지","dog");
            put("컴퓨터","laptop");
            put("노트북","laptop");
            put("마우스","mouse");
            put("가방","suitcase");
        }
    };
}
