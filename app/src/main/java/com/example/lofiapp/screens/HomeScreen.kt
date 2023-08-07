package com.example.lofiapp.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.lofiapp.R
import com.example.lofiapp.data.MenuAction
import com.example.lofiapp.ui.theme.flamenco_regular
import com.example.lofiapp.ui.theme.montserrat_bold
import com.example.lofiapp.ui.theme.montserrat_light
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavController, bp: String, bt: String) {

    // bottom bar
    var bottomBar_pic: String by remember { mutableStateOf(bp) }
    var bottomBar_title: String by remember { mutableStateOf(bt) }
    /*
    LaunchedEffect(bp){
        bottomBar_pic = bp
        bottomBar_title = bt
    }
    */
    Log.d(
        "video playing",
        "home_screen: initializeTop: " + bottomBar_title + " " + bottomBar_pic
    )

    // Pull to refresh components
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    var itemCount by remember { mutableStateOf(0) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        itemCount += 1
        delay(1500)
        refreshing = false
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)

    // vertical orientation
    val context = LocalContext.current
    val activity = remember { context as Activity }
    activity.requestedOrientation =
        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    val playlists = FirebaseDatabase.getInstance().getReference("playlists")
    val videos = FirebaseDatabase.getInstance().getReference("videos")

    // list of videos
    var recList by remember { mutableStateOf(mutableListOf<youtubeVideo?>()) }
    val playlist_List by remember { mutableStateOf(mutableListOf<single_playlist?>()) }

    // Read from the database
    LaunchedEffect(itemCount) {

        recList.clear()

        var randomInts = generateSequence { (1..15).random() }
            .distinct()
            .take(5)
            .toSet()

        randomInts.forEach() {
            videos.child("video" + it) // recommended videos
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        recList.add(dataSnapshot.getValue<youtubeVideo?>())
                        Log.d(
                            "RNG",
                            "added " + dataSnapshot.getValue<youtubeVideo?>()?.videoTitle.toString()
                        )
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }

        playlists.addValueEventListener(object : ValueEventListener {
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
                    IconButton(onClick = { navController.navigate("search_screen?bp=" + bottomBar_pic + "?bt=" + bottomBar_title ) }) {
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
                            .padding(start = 13.dp, top = 0.dp)
                            .height(IntrinsicSize.Max)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        recList.forEach {
                            if (recList.indexOf(it) == 4) {
                                return@forEach
                            }
                            Column(modifier = Modifier
                                .padding(start = 16.dp)
                                .clip(RoundedCornerShape(12, 12, 5, 5))
                                .clickable(
                                    // navigates to video screen
                                    onClick = {
                                        navController
                                            .navigate("video_screen/" + it?.videoID.toString())
                                            .apply {
                                                bottomBar_pic = it?.videoID.toString()
                                                bottomBar_title = it?.videoTitle.toString()
                                                Log.d(
                                                    "video playing",
                                                    "initialize: " + bottomBar_title + " " + bottomBar_pic
                                                )
                                            }
                                    }
                                )
                            ) { // Video Display
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
                                    } else {
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
                            navController.navigate("video_screen/" + recList[4]?.videoID.toString())
                                .apply {
                                    bottomBar_pic = recList[4]?.videoID.toString()
                                    bottomBar_title = recList[4]?.videoTitle.toString()
                                    Log.d(
                                        "video playing",
                                        "initialize: " + bottomBar_title + " " + bottomBar_pic
                                    )
                                }
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
            if (!bottomBar_title.equals("")) {
                Column() {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        BottomNavigation(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12))
                                .clickable {
                                    navController.navigate("video_screen/" + bottomBar_pic)
                                }
                                .fillMaxWidth(.95f),
                            backgroundColor = Color(0xFF3392EA),
                        ) {
                            Log.d(
                                "video playing",
                                "playing: " + bottomBar_title + " " + bottomBar_pic
                            )
                            Row(modifier = Modifier.fillMaxHeight()) { // wrap in row to avoid default spacing
                                Spacer(modifier = Modifier.width(16.dp))
                                AsyncImage( // video thumbnail
                                    model = "https://img.youtube.com/vi/" + bottomBar_pic + "/maxres2.jpg",
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(width = 65.dp, height = 43.dp)
                                        .clip(RoundedCornerShape(12))
                                        .align(Alignment.CenterVertically)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .fillMaxWidth(.70f)
                                        .fillMaxHeight(.95f),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text( // video name
                                        text = bottomBar_title,
                                        fontFamily = montserrat_bold,
                                        color = Color.White,
                                        modifier = Modifier
                                            .basicMarquee(),
                                        fontSize = 14.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                            Image( // play icon (note: make this a button)
                                painter = painterResource(R.drawable.play_arrow_icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(45.dp)
                                    .fillMaxHeight()
                                    .align(Alignment.CenterVertically)
                                    .clickable {
                                        navController.navigate("video_screen/" + bottomBar_pic)
                                    },
                                colorFilter = ColorFilter.tint(color = Color.White)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                        }
                    }
                    Spacer(
                        modifier = Modifier
                            .height(5.dp)
                    )
                }
            }
        }
    )

}




