package data;

/**
 * {@code MinimalpointIsLessThenZero} Ошибки, появляющиеся, если MinimalPoint меньше нуля.
 */
public class MinimalpointIsLessThenZero extends RuntimeException{

        public MinimalpointIsLessThenZero(){
            super("Минимальные баллы меньше 0");
        }
}
