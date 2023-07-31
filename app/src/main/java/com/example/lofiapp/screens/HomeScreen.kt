package com.example.lofiapp.screens

import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import android.app.Activity
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.lofiapp.R
import com.example.lofiapp.data.MenuAction
import com.example.lofiapp.data.ScreenRoutes
import com.example.lofiapp.ui.theme.LofiappTheme
import com.example.lofiapp.ui.theme.flamenco_regular
import com.example.lofiapp.ui.theme.montserrat_bold
import com.example.lofiapp.ui.theme.montserrat_light
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.random.nextInt


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavController, video_title: String, video_id: String) {

    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    var itemCount by remember { mutableStateOf(15) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(1500)
        itemCount += 5
        refreshing = false
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)

    val context = LocalContext.current
    val activity = remember { context as Activity }
    activity.requestedOrientation =
        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    val playlists = FirebaseDatabase.getInstance().getReference("playlists")
    val videos = FirebaseDatabase.getInstance().getReference("videos")
    var recVideo1: youtubeVideo? by remember { mutableStateOf(youtubeVideo("", "")) }
    var recVideo2: youtubeVideo? by remember { mutableStateOf(youtubeVideo("", "")) }
    var recVideo3: youtubeVideo? by remember { mutableStateOf(youtubeVideo("", "")) }
    var recVideo4: youtubeVideo? by remember { mutableStateOf(youtubeVideo("", "")) }
    var randomVideo: youtubeVideo? by remember { mutableStateOf(youtubeVideo("", "")) }

    // list of videos
    val list_ = listOf(recVideo1, recVideo2, recVideo3, recVideo4)
    //val list : List<youtubeVideo> = listOf()
    val recList by remember { mutableStateOf(mutableListOf<youtubeVideo?>())}
    val playlist_List by remember { mutableStateOf(mutableListOf<single_playlist?>())}

    // Read from the database
    LaunchedEffect(itemCount) {

        val randomInts = generateSequence { (1..15).random() }
            .distinct()
            .take(4)
            .toSet()

        Log.d("RNG", "" + randomInts.elementAt(0).toString())
        Log.d("RNG", "" + randomInts.elementAt(1).toString())
        Log.d("RNG", "" + randomInts.elementAt(2).toString())
        Log.d("RNG", "" + randomInts.elementAt(3).toString())


        videos.child("video" + randomInts.elementAt(0).toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    recVideo1 = dataSnapshot.getValue<youtubeVideo>()
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        videos.child("video" + randomInts.elementAt(1).toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    recVideo2 = dataSnapshot.getValue<youtubeVideo>()
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        videos.child("video" + randomInts.elementAt(2).toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    recVideo3 = dataSnapshot.getValue<youtubeVideo>()
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        videos.child("video" + randomInts.elementAt(3).toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    recVideo4 = dataSnapshot.getValue<youtubeVideo>()
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        videos.child("video" + (1..15).random())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    randomVideo = dataSnapshot.getValue<youtubeVideo>()
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        videos.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    recList.add(childSnapshot.getValue<youtubeVideo?>())
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        playlists.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    Log.d("add playlist", "added")
                    playlist_List.add(childSnapshot.getValue<single_playlist?>())
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })



    }

    // System bar colors
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = Color(0xFF24CAAC))         // System top bar color
    systemUiController.setNavigationBarColor(color = Color(0xFF24CAAC))     // System bottom bar color

    // Scroll State (used for vertical scrolling)
    val scrollState = rememberScrollState()

    // Scaffold (Top bar, Content, Bottom Bar)
    Scaffold(
        topBar = {
            // Top Bar Composable
            TopAppBar(
                title = {
                    Text(
                        text = "lofiapp",
                        color = Color.White,
                        fontFamily = flamenco_regular,
                        fontSize = 32.sp
                    )
                },
                backgroundColor = Color(0xFF24CAAC),
                actions = {
                    // Create Playlist button
                    IconButton(
                        onClick = { navController.navigate("playlist_screen/new_playlist") }
                    ) {
                        Image( // Search icon
                            painter = painterResource(id = R.drawable.playlist_add_icon),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = Color.White),
                            modifier = Modifier
                                .size(35.dp)
                        )
                    }
                    // Search Button - navigates to search screen
                    IconButton(onClick = { navController.navigate(ScreenRoutes.SearchScreen.route) }) {
                        Icon( // Search icon
                            imageVector = MenuAction.Search.icon,
                            contentDescription = stringResource(MenuAction.Search.label),
                            tint = Color.White,
                            modifier = Modifier
                                .size(30.dp)
                        )
                    }
                }
            )
        },
        // Content
        content = { padding ->
            Box(Modifier.pullRefresh(state)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(state = scrollState) // scrollState here
                    ,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    // Recommended Text
                    Row(modifier = Modifier.padding(top = 45.dp, start = 30.dp)) {
                        Text(
                            text = "Recommended",
                            color = Color(0xFF24CAAC),
                            modifier = Modifier,
                            fontSize = 21.sp,
                            fontFamily = montserrat_bold
                        )
                        Image( // Symbol
                            painter = painterResource(id = R.drawable.video_library_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(34.dp),
                            colorFilter = ColorFilter.tint(color = Color(0xFF5686E1))
                        )
                    }

                    // Recommended Videos Display (horz. scroll)
                    Row(
                        modifier = Modifier
                            .padding(start = 13.dp, top = 6.dp)
                            .height(IntrinsicSize.Max)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Log.d("add video", "hello?")
                        recList.forEach {
                            Log.d("add video", "title = " + it?.videoTitle.toString())
                            Column(modifier = Modifier
                                .padding(start = 16.dp)
                                .clip(RoundedCornerShape(12, 12, 5, 5))
                                .combinedClickable(
                                    // navigates to video screen
                                    onClick = { navController.navigate("video_screen/" + it?.videoID.toString()) },
                                    onLongClick = {}
                                )
                            ) { // Video Display
                                // {
                                AsyncImage( // Video thumbnail
                                    model = "https://img.youtube.com/vi/" + it?.videoID.toString() + "/maxres2.jpg",
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(width = 220.dp, height = 134.dp)
                                        .clip(RoundedCornerShape(12))
                                )
                                Text( // Video name
                                    text = it?.videoTitle.toString(),
                                    maxLines = 4,
                                    modifier = Modifier
                                        .width(220.dp),
                                    fontSize = 16.sp,
                                    fontFamily = montserrat_light
                                )
                            }
                        }
                    }

                    // Playlists Title Text
                    Row(modifier = Modifier.padding(top = 30.dp, start = 29.dp)) {
                        Text(
                            text = "Playlists",
                            color = Color(0xFF24CAAC),
                            modifier = Modifier,
                            fontSize = 21.sp,
                            fontFamily = montserrat_bold
                        )
                        Image( // Symbol
                            painter = painterResource(id = R.drawable.playlist_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(34.dp),
                            colorFilter = ColorFilter.tint(color = Color(0xFF5686E1))
                        )
                    }

                    // Playlists display (horz. scroll)
                    LazyRow(modifier = Modifier.padding(start = 13.dp, top = 6.dp)) {
                        items(items = playlist_List, itemContent = { item ->
                            Column(
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .clip(RoundedCornerShape(12, 12, 5, 5))
                                    .clickable {
                                        navController.navigate("playlist_screen/" + item?.playlistTitle.toString())
                                    }
                            ) {
                                Box() {
                                    if (!item?.playlistTitle.equals("Create New Playlist")) {
                                        AsyncImage( // Video Thumbnail
                                            model = "https://img.youtube.com/vi/" + item?.playlistTitle.toString() + "/maxres2.jpg",
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(width = 220.dp, height = 134.dp)
                                                .clip(RoundedCornerShape(12))
                                        )
                                    }
                                    else {
                                        Image(
                                            painter = painterResource(id = R.drawable.add_new_icon),
                                            colorFilter = ColorFilter.tint(Color.White),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(width = 220.dp, height = 134.dp)
                                                .clip(RoundedCornerShape(12))
                                                .background(Color.Black.copy(alpha = 0.6f))
                                        )
                                    }
                                    if (item?.playlistCount!! > 0) {
                                        Box( // Transparent background
                                            modifier = Modifier
                                                .size(width = 105.dp, height = 134.dp)
                                                .clip(RoundedCornerShape(0.dp, 15.dp, 15.dp, 0.dp))
                                                .background(Color(0xFF404040).copy(alpha = 0.6f))
                                                .align(alignment = Alignment.TopEnd)
                                        ) {
                                            Text( // Number of videos in playlist
                                                text = item.playlistCount.toString(),
                                                modifier = Modifier
                                                    .align(alignment = Alignment.Center),
                                                color = Color.White,
                                                fontFamily = montserrat_light
                                            )
                                        }
                                    }
                                }
                                Text( // Playlist Name
                                    text = item?.playlistTitle.toString(),
                                    maxLines = 2,
                                    modifier = Modifier
                                        .width(220.dp)
                                        .height(IntrinsicSize.Max),
                                    fontSize = 16.sp,
                                    fontFamily = montserrat_light
                                )
                            }
                        })
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    // Random video button
                    FloatingActionButton(
                        onClick = {
                            navController.navigate("video_screen/" + randomVideo?.videoID.toString())
                        },
                        contentColor = Color(0xFF24CAAC),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .size(75.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 0.dp, bottom = 0.dp)
                    )
                    {
                        Image( // shuffle symbol
                            painter = painterResource(id = R.drawable.shuffle_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            colorFilter = ColorFilter.tint(color = Color.White)
                        )
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }

                PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
            }
        },
        bottomBar = {
            // Bottom bar (Displays what video is played)
            BottomNavigation(
                modifier = Modifier
                    .clickable {
                        navController.navigate(ScreenRoutes.VideoScreen.route)
                    },
                backgroundColor = Color(0xFF3392EA)
            ) {
                Row() { // wrap in row to avoid default spacing
                    AsyncImage( // video thumbnail
                        model = "https://img.youtube.com/vi/" + "" + "/hqdefault.jpg",
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 6.dp)
                            .size(width = 65.dp, height = 43.dp)
                            .clip(RoundedCornerShape(12))
                    )
                    Box(
                        modifier = Modifier
                            .size(width = 140.dp, height = 53.dp)
                            .padding(top = 3.dp)
                    ) {
                        Text( // video name
                            text = "lofi hip hop radio \uD83D\uDCDA - beats to relax/study to",
                            fontFamily = montserrat_bold,
                            color = Color.White,
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .fillMaxSize(),
                            fontSize = 10.sp
                        )
                    }
                }
                Image( // play icon (note: make this a button)
                    painter = painterResource(R.drawable.play_circle_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 4.dp, end = 16.dp)
                        .size(45.dp)
                        .clip(RoundedCornerShape(75, 75, 75, 75))
                        .clickable {
                            navController.navigate(ScreenRoutes.VideoScreen.route)
                        },
                    colorFilter = ColorFilter.tint(color = Color.White)
                )
            }
        }
    )

}



