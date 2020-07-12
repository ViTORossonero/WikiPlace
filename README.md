# WikiPlace

### Notes: 
 * Test task was implemented in May of 2019. This was my first experience with coroutines(but I used RxJava a lot);
 * `Flow` was experimental at that moment.
 
### Description

Приложение получает текущую геопозицию устройства и запрашивает с Wikipedia список статей по этой локации (не более 50).
Эти статьи нужно вывести в списке, в котором каждый элемент состоит из двух строк:
* Заголовок статьи;
* Количество изображений в формате: "N изображений".

Требования:
- Поддержка Android 6.0+.
- Kotlin coroutines.
- Не использовать Rx.
- Опционально: использовать любую DI библиотеку.

Ссылки:
* Геопоиск: https://www.mediawiki.org/wiki/API:Nearby_places_viewer
* Получение изображений: https://www.mediawiki.org/wiki/API:Images
* Как работать с пагинацией: https://www.mediawiki.org/wiki/API:Raw_query_continue
