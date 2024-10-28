package com.example.mealjsonexample


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import coil3.compose.AsyncImage

@Composable
fun Navigation(
    modifier: Modifier,
    navigationController: NavHostController,
) {
    val viewModel: MealsViewModel = viewModel()
    NavHost(
        modifier = modifier,
        navController = navigationController,
        startDestination = Graph.mainScreen.route
    ) {
        composable(route = Graph.mainScreen.route) {
            MainScreen(viewModel, navigationController)
        }
        composable(route = Graph.secondScreen.route) {
            SecondScreen(viewModel, navigationController)
        }
        composable(route = Graph.mealDetailScreen.route) { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId") ?: ""
            MealDetailScreen(mealId)
        }
    }
}


@Composable
fun SecondScreen(viewModel: MealsViewModel, navigationController: NavHostController) {
    val categoryName = viewModel.chosenCategoryName.collectAsState()
    val dishesState = viewModel.mealsState.collectAsState()
    viewModel.getAllDishesByCategoryName(categoryName.value)
    Column {
        if (dishesState.value.isLoading) {
            LoadingScreen()
        }
        if (dishesState.value.isError) {
            ErrorScreen(dishesState.value.error!!)
        }
        if (dishesState.value.result.isNotEmpty()) {
            DishesScreen(dishesState.value.result, navigationController)
        }
    }
}


@Composable
fun DishesScreen(result: List<Meal>, navigationController: NavHostController) {
    LazyColumn(modifier = Modifier.padding(8.dp).background(Color.Gray)) {
        items(result, key = { meal -> meal.idMeal }) { meal ->
            DishItem(meal, navigationController)
        }
    }
}

@Composable
fun DishItem(meal: Meal, navigationController: NavHostController) {
    Card(
        modifier = Modifier
            .background(color = Color.White)
            .padding(8.dp)
            .shadow(4.dp)
            .border(2.dp, Color.Gray, shape = RoundedCornerShape(12.dp))
            .clickable {
                navigationController.navigate("mealDetail/${meal.idMeal}")
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                modifier = Modifier.height(150.dp).fillMaxWidth(),
                model = meal.strMealThumb,
                contentDescription = null
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = meal.mealName,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
        }
    }
}

@Composable
fun MainScreen(viewModel: MealsViewModel, navigationController: NavHostController) {
    val categoriesState = viewModel.categoriesState.collectAsState()
    val mealsByAreaState = viewModel.mealsByAreaState.collectAsState()
    val randomMealState = viewModel.randomMeal.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color.LightGray).padding(16.dp)) {
        TextField(
            value = viewModel.areaQuery.collectAsState().value,
            onValueChange = { viewModel.setAreaQuery(it) },
            placeholder = { Text("Поиск по области") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { viewModel.getAllDishesByArea(viewModel.areaQuery.value) },
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Поиск")
            }

            Button(
                onClick = { viewModel.fetchRandomMeal() },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Случайное блюдо")
            }
        }

        Box(modifier = Modifier.fillMaxSize().background(Color.LightGray)) {
            if (categoriesState.value.isLoading) {
                LoadingScreen()
            }
            if (categoriesState.value.isError) {
                ErrorScreen(categoriesState.value.error!!)
            }
            if (categoriesState.value.result.isNotEmpty()) {
                CategoriesScreen(viewModel, categoriesState.value.result, navigationController)
            }

            if (mealsByAreaState.value.isLoading) {
                LoadingScreen()
            }
            if (mealsByAreaState.value.isError) {
                ErrorScreen(mealsByAreaState.value.error!!)
            }
            if (mealsByAreaState.value.result.isNotEmpty()) {
                DishesScreen(mealsByAreaState.value.result, navigationController)
            }

            if (randomMealState.value.isLoading) {
                LoadingScreen()
            }
            if (randomMealState.value.isError) {
                ErrorScreen(randomMealState.value.error!!)
            }
            if (randomMealState.value.result.isNotEmpty()) {
                MealCard(randomMealState.value.result.first(),navigationController)
            }
        }
    }
}

@Composable
fun MealCard(meal: Meal, navigationController: NavHostController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .shadow(8.dp, shape = RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (meal.strMealThumb.isNotEmpty()) {
                AsyncImage(
                    model = meal.strMealThumb,
                    contentDescription = meal.mealName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Изображение недоступно",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Text(
                text = meal.mealName,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Button(
                onClick = { navigationController.navigate("mealDetail/${meal.idMeal}") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Посмотреть id")
            }
        }
    }
}

@Composable
fun CategoriesScreen(viewModel: MealsViewModel, result: List<Category>, navigationController: NavHostController) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2)
    ) {
        items(result){
            CategoryItem(viewModel, it, navigationController)
        }
    }
}

@Composable
fun CategoryItem(viewModel: MealsViewModel, category: Category, navigationController: NavHostController) {
    Card(
        modifier = Modifier.height(200.dp).background(color = Color.LightGray).padding(8.dp).shadow(4.dp)
            .clickable {
                viewModel.setChosenCategory(category.strCategory)
                navigationController.navigate(Graph.secondScreen.route)
            }
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = category.strCategoryThumb,
                contentDescription = null ,
                modifier = Modifier.fillMaxSize()
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = category.strCategory,
                style= MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun ErrorScreen(error: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = error
        )
    }
}

@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun MealDetailScreen(mealId: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "ID: $mealId", style = MaterialTheme.typography.titleLarge)
    }
}
