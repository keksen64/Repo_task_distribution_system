# Repo_task_distribution_system
## Что это?
Это мое бэкэнд приложение, для  регистрации и распределения задач по пользователям.
Задачи распределяются на пользователей в соответствии с принадлежностью задачи/пользователя к определенным бизнес процессам, типам,
а также в соответствии с необходимыми для разрешения задачи компетенциями (которые определяются по правилам принадлежности к различным типам).
Приложение состоит из субд и двух сервисов.
На данный момент в приложении реализован следующий бизнес-процесс(жизненный цикл задачи):
1) пост-запрос. Пользователь переходит по запросу в состояние эктив
2) пост-запрос. идёт запрос на распределение задачи (в базе на пользователя назначается задача. в ответ приходит ее айди). Также происходит перевод задачи в соответствующий статус "назначена" и ей присваивается исполнитель. Регистрируется факт назначения задачи в истории. Блокируется возможность дальнейшего назначения на пользака задач.
3) гет-запрос. запрос назначенных на пользователя задач (в ответ приходит айди задачи из пункта 2)
4) гет-запрос. Запрос получения задачи по ее айди (в ответ возвращается модель задачи)
5) пост-запрос. Запрос на перевод статуса задачи на "в работе". Задача меняет статус. Событие фиксируется в стории задачи.
6) пост-запрос. Запрос на перевод статуса задачи в "решена". В историю записывается факт решения задачи. На задаче фиксируется разрешивший ее пользователь. Сам пользователь становится доступен для нового распределения задач.

Кроме основного бизнес процесса в системе реализован
1) метод регистрации задач. Заводит задачу и заносит запись о регистрации в журнал
2) журнал истории задач, в который фиксируются все события связанные с задчами
3) набор различных бизнес процессов и компетенций (как для задач, так и для пользователей)
4) реализованы логические ограничения, такие как исключение возможности назначения задач на не активных пользователей, на пользователей у которых уже есть задача, снятие задач с пользователей при переходе в не активный статус и прочие ограничения.
5) обеспечивается транзакционность и целостность данных при асинхронной работе пользователей в системе. Исключены возможности любых ошибок(в том числе на уровне бизнес-логики) при попытке одновременной работы нескольких пользователей с одним объектом.

## Бэклог
1) добавить миграцию и раскатку структуры модели данных на чистую базу
2) добавить раскатку минимально необходимых тестовых данных
3) заменить переменные классов юзер и таск на приватные. Перевести всю работу с полями данных классов на гетеры и сетеры
4) добавить схему модели данных
5) добавить в репозиторий скрипт НТ

## Попробовать


## Автор
Павлов Захарий

## Контакты для связи
Telegram [@kekss_pekss](https://t.me/kekss_pekss)



## Сборка и запуск

