import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CardDeliveryTest {

    public String generateDate(int addDays, String pattern) {
        return LocalDate.now().plusDays(addDays).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Test
    void happyPath() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999/");
        $("div [data-test-id='city'] input").setValue("Санкт-Петербург");
        $("div [data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("div [data-test-id='date'] input").setValue(generateDate(3, "dd.MM.yyyy"));
        $("div [data-test-id='name'] input").setValue("Петров Андрей");
        $("div [data-test-id='phone'] input").setValue("+79194885621");
        $("[data-test-id='agreement']").click();
        $(".button__text").click();
        $("[data-test-id='notification']").should(visible, Duration.ofSeconds(15));
        $("div.notification__title").shouldHave(exactText("Успешно!"));
        $("div.notification__content").shouldHave(exactText("Встреча успешно забронирована на " + generateDate(3, "dd.MM.yyyy")));
    }

    @Test
    void shouldValidateAllLettersOfRussianAlphabet() {
        open("http://localhost:9999/");
        $("div [data-test-id='city'] input").setValue("Санкт-Петербург");
        $("div [data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("div [data-test-id='date'] input").setValue(generateDate(3, "dd.MM.yyyy"));
        $("div [data-test-id='name'] input").setValue("Царёва Алёна");
        $("div [data-test-id='phone'] input").setValue("+79194885621");
        $("[data-test-id='agreement']").click();
        $(".button__text").click();
        $("[data-test-id='notification']").should(visible, Duration.ofSeconds(15));
        $("div.notification__title").shouldHave(exactText("Успешно!"));
        $("div.notification__content").shouldHave(exactText("Встреча успешно забронирована на " + generateDate(3, "dd.MM.yyyy")));
    }

    @Test
    void shouldValidateHyphenatedName() {
        open("http://localhost:9999/");
        $("div [data-test-id='city'] input").setValue("Санкт-Петербург");
        $("div [data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("div [data-test-id='date'] input").setValue(generateDate(3, "dd.MM.yyyy"));
        $("div [data-test-id='name'] input").setValue("Петров-Водкин Кузьма");
        $("div [data-test-id='phone'] input").setValue("+79194885621");
        $("[data-test-id='agreement']").click();
        $(".button__text").click();
        $("[data-test-id='notification']").should(visible, Duration.ofSeconds(15));
        $("div.notification__title").shouldHave(exactText("Успешно!"));
        $("div.notification__content").shouldHave(exactText("Встреча успешно забронирована на " + generateDate(3, "dd.MM.yyyy")));
    }

    @Test
    void shouldSendFormIfScheduledDateIsLaterThan3Days() {
        open("http://localhost:9999/");
        $("div [data-test-id='city'] input").setValue("Санкт-Петербург");
        $("div [data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("div [data-test-id='date'] input").setValue(generateDate(4, "dd.MM.yyyy"));
        $("div [data-test-id='name'] input").setValue("Петров Андрей");
        $("div [data-test-id='phone'] input").setValue("+79194885621");
        $("[data-test-id='agreement']").click();
        $(".button__text").click();
        $("[data-test-id='notification']").should(visible, Duration.ofSeconds(15));
        $("div.notification__title").shouldHave(exactText("Успешно!"));
        $("div.notification__content").shouldHave(exactText("Встреча успешно забронирована на " + generateDate(4, "dd.MM.yyyy")));
    }

    @Test
    void shouldNotSendFormWhenInvalidCity() {
        open("http://localhost:9999/");
        $("div [data-test-id='city'] input").setValue("Сочи");
        $("div [data-test-id='date'] input").setValue(generateDate(3, "dd.MM.yyyy"));
        $("div [data-test-id='name'] input").setValue("Петров Андрей");
        $("div [data-test-id='phone'] input").setValue("+79194885621");
        $("[data-test-id='agreement']").click();
        $(".button__text").click();
        $("[data-test-id='city'] span.input__sub").shouldHave(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldNotSendFormIfScheduledDateIsEarlierThan3Days() {
        open("http://localhost:9999/");
        $("div [data-test-id='city'] input").setValue("Санкт-Петербург");
        $("div [data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("div [data-test-id='date'] input").setValue(generateDate(2, "dd.MM.yyyy"));
        $("div [data-test-id='name'] input").setValue("Петров Андрей");
        $("div [data-test-id='phone'] input").setValue("+79194885621");
        $("[data-test-id='agreement']").click();
        $(".button__text").click();
        $("[data-test-id='date'] span.input__sub").shouldHave(exactText("Заказ на выбранную дату невозможен"));
    }

    @Test
    void shouldNotSendFormIfNameContainsLatin() {
        open("http://localhost:9999/");
        $("div [data-test-id='city'] input").setValue("Санкт-Петербург");
        $("div [data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("div [data-test-id='date'] input").setValue(generateDate(3, "dd.MM.yyyy"));
        $("div [data-test-id='name'] input").setValue("Petrov Andrey");
        $("div [data-test-id='phone'] input").setValue("+79194885621");
        $("[data-test-id='agreement']").click();
        $(".button__text").click();
        $("[data-test-id='name'] span.input__sub").shouldHave(exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldNotSendFormIfNameContainsNumbers() {
        open("http://localhost:9999/");
        $("div [data-test-id='city'] input").setValue("Санкт-Петербург");
        $("div [data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("div [data-test-id='date'] input").setValue(generateDate(3, "dd.MM.yyyy"));
        $("div [data-test-id='name'] input").setValue("Петр0в Андрей");
        $("div [data-test-id='phone'] input").setValue("+79194885621");
        $("[data-test-id='agreement']").click();
        $(".button__text").click();
        $("[data-test-id='name'] span.input__sub").shouldHave(exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldNotSendFormIfPhoneNumberStartsWithoutPlus() {
        open("http://localhost:9999/");
        $("div [data-test-id='city'] input").setValue("Санкт-Петербург");
        $("div [data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("div [data-test-id='date'] input").setValue(generateDate(3, "dd.MM.yyyy"));
        $("div [data-test-id='name'] input").setValue("Петров Андрей");
        $("div [data-test-id='phone'] input").setValue("89194885621");
        $("[data-test-id='agreement']").click();
        $(".button__text").click();
        $("[data-test-id='phone'] span.input__sub").shouldHave(exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldNotSendFormIfPhoneNumberIsAbove11Numbers() {
        open("http://localhost:9999/");
        $("div [data-test-id='city'] input").setValue("Санкт-Петербург");
        $("div [data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("div [data-test-id='date'] input").setValue(generateDate(3, "dd.MM.yyyy"));
        $("div [data-test-id='name'] input").setValue("Петров Андрей");
        $("div [data-test-id='phone'] input").setValue("+123456789101");
        $("[data-test-id='agreement']").click();
        $(".button__text").click();
        $("[data-test-id='phone'] span.input__sub").shouldHave(exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldNotSendFormIfPhoneNumberIsBelow11Numbers() {
        open("http://localhost:9999/");
        $("div [data-test-id='city'] input").setValue("Санкт-Петербург");
        $("div [data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("div [data-test-id='date'] input").setValue(generateDate(3, "dd.MM.yyyy"));
        $("div [data-test-id='name'] input").setValue("Петров Андрей");
        $("div [data-test-id='phone'] input").setValue("+1234567890");
        $("[data-test-id='agreement']").click();
        $(".button__text").click();
        $("[data-test-id='phone'] span.input__sub").shouldHave(exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldNotSendFormIfPhoneContainsLetters() {
        open("http://localhost:9999/");
        $("div [data-test-id='city'] input").setValue("Санкт-Петербург");
        $("div [data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("div [data-test-id='date'] input").setValue(generateDate(3, "dd.MM.yyyy"));
        $("div [data-test-id='name'] input").setValue("Петров Андрей");
        $("div [data-test-id='phone'] input").setValue("Андрей");
        $("[data-test-id='agreement']").click();
        $(".button__text").click();
        $("[data-test-id='phone'] span.input__sub").shouldHave(exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldNotSendFormIfEmptyField1() {
        open("http://localhost:9999/");
        $("div [data-test-id='city'] input").setValue("");
        $("div [data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("div [data-test-id='date'] input").setValue(generateDate(3, "dd.MM.yyyy"));
        $("div [data-test-id='name'] input").setValue("Петров Андрей");
        $("div [data-test-id='phone'] input").setValue("+79194885621");
        $("[data-test-id='agreement']").click();
        $(".button__text").click();
        $("[data-test-id='city'] span.input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldNotSendFormIfEmptyField2() {
        open("http://localhost:9999/");
        $("div [data-test-id='city'] input").setValue("Санкт-Петербург");
        $("div [data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("div [data-test-id='name'] input").setValue("Петров Андрей");
        $("div [data-test-id='phone'] input").setValue("+79194885621");
        $("[data-test-id='agreement']").click();
        $(".button__text").click();
        $("[data-test-id='date'] span.input__sub").shouldHave(exactText("Неверно введена дата"));
    }

    @Test
    void shouldNotSendFormIfEmptyField3() {
        open("http://localhost:9999/");
        $("div [data-test-id='city'] input").setValue("Санкт-Петербург");
        $("div [data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("div [data-test-id='date'] input").setValue(generateDate(3, "dd.MM.yyyy"));
        $("div [data-test-id='name'] input").setValue("");
        $("div [data-test-id='phone'] input").setValue("+79194885621");
        $("[data-test-id='agreement']").click();
        $(".button__text").click();
        $("[data-test-id='name'] span.input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldNotSendFormIfEmptyField4() {
        open("http://localhost:9999/");
        $("div [data-test-id='city'] input").setValue("Санкт-Петербург");
        $("div [data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("div [data-test-id='date'] input").setValue(generateDate(3, "dd.MM.yyyy"));
        $("div [data-test-id='name'] input").setValue("Петров Андрей");
        $("div [data-test-id='phone'] input").setValue("");
        $("[data-test-id='agreement']").click();
        $(".button__text").click();
        $("[data-test-id='phone'] span.input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldNotSendFormIfEmptyCheckbox() {
        open("http://localhost:9999/");
        $("div [data-test-id='city'] input").setValue("Санкт-Петербург");
        $("div [data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("div [data-test-id='date'] input").setValue(generateDate(3, "dd.MM.yyyy"));
        $("div [data-test-id='name'] input").setValue("Петров Андрей");
        $("div [data-test-id='phone'] input").setValue("+79194885621");
        $(".button__text").click();
        $("[data-test-id='agreement'].input_invalid .checkbox__text").shouldHave(exactText("Я соглашаюсь с условиями обработки и использования моих персональных данных"));
    }
}





