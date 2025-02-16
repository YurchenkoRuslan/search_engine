package searchengine.utils;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtils {

    //метод получает список списков элементов и возвращает список элементов, которые есть в каждом списке
    public static <T> List<T> getCommonElementsList(List<List<T>> lists) {
        if (lists.isEmpty())
            return new ArrayList<>();
        List<T> resultList = new ArrayList<>(lists.get(0));
        List<T> buferList = new ArrayList<>();
        for(List<T> list : lists){
            for (T iObj : resultList) {
                boolean flag = false;
                for (T kObj : list) {
                    if (flag) break;
                    if (iObj.equals(kObj)) {
                        buferList.add(iObj);
                        flag = true;
                    }
                }
            }
            resultList = new ArrayList<>(buferList);
            buferList = new ArrayList<>();
        }
        return resultList;
    }

}
