package diethelper.views

import scalatags.Text
import scalatags.Text.all._
import diethelper.views.htmx.HtmxAttributes

object RecipeView {

  // Function to render individual product input field
  def productField(
      productId: Int,
      newValue: String = "",
      error: Option[String] = None
  ) = {
    div(cls := "product-field", id := s"product-field-$productId")(
      label(`for` := s"products_$productId", s"Product ${productId + 1}"),
      input(
        name := "products[]",
        id := s"products_$productId",
        `type` := "text",
        placeholder := "Product",
        value := newValue
      ),
      error.map(err => span(cls := "error", err)),
      button(
        `type` := "button",
        cls := "remove-product",
        HtmxAttributes.trigger("click"),
        HtmxAttributes.swap("outerHTML"),
        HtmxAttributes.target(s"#product-field-$productId"),
        "Remove"
      )
    )
  }

  def newRecipeForm(
      previousFormValues: Map[String, String] = Map.empty,
      errors: Map[String, String] = Map.empty
  ): Text.TypedTag[String] = {
    div(
      `class` := "container",
      form(
        action := "/recipe/new",
        method := "post",
        fieldset(
          legend("Recipe details"),
          p(
            label(`for` := "name", "Name"),
            input(
              name := "name",
              id := "name",
              `type` := "text",
              placeholder := "Name",
              value := previousFormValues.getOrElse("name", "")
            ),
            errors.get("name").map(name => span(cls := "error", name))
          ),
          p(
            label(`for` := "calories", "Calories"),
            input(
              name := "calories",
              id := "calories",
              `type` := "number",
              step := "0.01",
              placeholder := "Calories",
              value := previousFormValues.getOrElse("calories", "")
            ),
            errors.get("calories").map(name => span(cls := "error", name))
          ),
          p(
            label(`for` := "description", "Description"),
            textarea(
              name := "description",
              id := "description",
              placeholder := "Description",
              value := previousFormValues.getOrElse("description", "")
            ),
            errors.get("description").map(email => span(cls := "error", email))
          ),
          // p(
          //   label(`for` := "products", "Products"),
          //   textarea(
          //     name := "products",
          //     id := "products",
          //     // `type` := "text",
          //     placeholder := "Products",
          //     value := previousFormValues.getOrElse("products", "")
          //   ),
          //   errors.get("products").map(email => span(cls := "error", email))
          // ),
          div(id := "products-container")(
            // Initialize with existing product values or a single empty field
            if (previousFormValues.contains("products")) {
              previousFormValues("products").split(",").zipWithIndex.map {
                case (product, index) =>
                  productField(index, product, errors.get(s"products_$index"))
              }
            } else {
              Seq(productField(0))
            }
          ),
          button(
            `type` := "button",
            cls := "add-product",
            HtmxAttributes.trigger("click"),
            HtmxAttributes.get("/recipe/new/addProductField"),
            HtmxAttributes.target("#products-container"),
            HtmxAttributes.swap("beforeend"),
            "Add Product"
          ),
          button("Save")
        )
      ),
      p(
        a(href := "/recipes", "Back")
      )
    )
  }
}
