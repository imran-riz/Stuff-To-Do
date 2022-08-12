/*
 * Copyright (c) 2022 by Imran R.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package util;

import models.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IdGenerator {

    public static String generateNewId() {
        return getFirstLtrsForId().concat("00000") ;
    }

    public static String generateIdForRepeatedTask(Task task) {
        String id, idDigits ;
        int n ;

        n = Integer.parseInt(task.getId().substring(4, 9)) + 1;

        if (n < 10) {
            idDigits = "0000".concat(Integer.toString(n));
        }
        else if (n < 100) {
            idDigits = "000".concat(Integer.toString(n)) ;
        }
        else if (n < 1000) {
            idDigits = "00".concat(Integer.toString(n)) ;
        }
        else if (n < 10000) {
            idDigits = "0".concat(Integer.toString(n)) ;
        }
        else {
            idDigits = Integer.toString(n) ;
        }

        id = task.getId().substring(0, 4).concat(idDigits);

        return id ;
    }


    private static String getFirstLtrsForId() {
        String newIdLtrs = "" ;
        Random random = new Random() ;

        List<Task> listOfTasks = TaskCollection.getInstance().getAllTasks() ;
        List<String> listOfIds = new ArrayList<>() ;

        for (int i = 0; i < listOfTasks.size(); i++) {
            String currentIdLtrs = listOfTasks.get(i).getId().substring(0, 4) ;

            if (i > 0 && !listOfIds.isEmpty()) {
                if (!currentIdLtrs.equalsIgnoreCase(listOfIds.get(listOfIds.size()-1).substring(0, 4))) {
                    listOfIds.add(currentIdLtrs) ;
                }
            }
            else {
                listOfIds.add(currentIdLtrs) ;
            }
        }

        do {
            for (int x = 0; x < 4 ; x++) {
                int n = random.nextInt(65, 91) ;
                newIdLtrs = newIdLtrs.concat(Character.toString((char) n)) ;
            }
        }
        while (listOfIds.contains(newIdLtrs)) ;

        return newIdLtrs;
    }
}
