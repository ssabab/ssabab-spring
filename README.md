# ssabab_spring


## ERD
![alt text](./images/image(2).png)

## API

### Swagger
[OpenAPI](http://localhost:8080/swagger-ui/index.html)


### [1] MENU

- URL : `http://localhost:8080/menu/{date}`
- Method : `POST`, `GET`, `PUT`, `DELETE`

- Header : `None`

- Body (POST, PUT)
    ```txt
    [
        {
            "foods": [
                {
                    "foodName": "된장찌개",
                    "mainSub": "주메뉴",
                    "category": "한식",
                    "tag": "국물"
                },
                {
                    "foodName": "잡곡밥",
                    "mainSub": "서브메뉴",
                    "category": "한식",
                    "tag": "밥"
                },
                {
                    "foodName": "포기김치",
                    "mainSub": "서브메뉴",
                    "category": "한식",
                    "tag": "야채"
                }
            ]
        },
        {
            "foods": [
                {
                    "foodName": "김치찌개",
                    "mainSub": "주메뉴",
                    "category": "한식",
                    "tag": "국물"
                },
                {
                    "foodName": "잡곡밥",
                    "mainSub": "서브메뉴",
                    "category": "한식",
                    "tag": "밥"
                },
                {
                    "foodName": "포기김치",
                    "mainSub": "서브메뉴",
                    "category": "한식",
                    "tag": "야채"
                }
            ]
        }
    ]
    ```


### [2] Review

- URL : `http://localhost:8080/review/{menu_id}`
- Method : `GET`, `POST`, `PUT`, `DELETE`
- Header
    | Key | Value|
    | --- | --- |
    | **Content-Type** | application/json |
    | **user_id** | user_id |

- Body (POST, PUT)

    ```txt
    {
        "foods": [
            {
            "foodId": 1,
            "foodName": "된장찌개",
            "foodScore": 4
            },
            {
            "foodId": 2,
            "foodName": "잡곡밥",
            "foodScore": 3
            },
            {
            "foodId": 3,
            "foodName": "포기김치",
            "foodScore": 2
            }
        ]
    }

    ```


### [3] Account
