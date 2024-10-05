# Notes

## Domain

### Recipe

| Field Name | Type                      | Example                           | Description                                                     |
| ---------- | ------------------------- | --------------------------------- | --------------------------------------------------------------- |
| Name       | String                    | Spaghetti                         | Name of the recipe                                              |
| Steps      | String                    | 1. ..., 2. ...                    | How to make the recipe                                          |
| Products   | List[(MyProduct, Double)] | List((Tomato, 10), (Potato, 100)) | List of the products with the mass, required to make the recipe |
| Calories   | Double                    | 650.2                             | Number of calories for this recipe                              |


### Product
| Field Name | Type         | Example | Description              |
| ---------- | ------------ | ------- | ------------------------ |
| Name       | String       | Tomato  | Name of the product      |
| Category   | ListCategory | Fridge  | Category for the product |