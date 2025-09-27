package com.example.bankcards.service.card;

import com.example.bankcards.util.CardUtils;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class NumberGenerator {
    private static final Random random = new Random();

    public static String generateNumber() {                 // 8800 5553 5356 7464
        StringBuilder number = new StringBuilder();
        number.append(CardUtils.BANK_BIN);
                                                            // toDo: алгоритм Луна?
        for (int i = 0; i < 12; i++) {
            if (i == 2 || i == 7) {
                number.append(" ");
                continue;
            }
            int num = random.nextInt(10);
            number.append(num);
        }
        return number.toString();
    }
}
