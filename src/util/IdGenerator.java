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

import java.util.Random;

public class IdGenerator {

    public static String generateId() {
        Random random = new Random() ;
        String id = "";
        int n ;

        for (int i = 0 ; i < 5 ; i++) {
            if (i < 2) {
                n = random.nextInt(65, 91) ;
                id = id.concat(String.valueOf((char) n)) ;
            }
            else {
                n = random.nextInt(1, 10) * random.nextInt(1, 10) * 31 ;
                n = (n * n) / 31 ;
                n = n % 10 ;

                id = id.concat(String.valueOf(n)) ;
            }
        }

        return id ;
    }
}
