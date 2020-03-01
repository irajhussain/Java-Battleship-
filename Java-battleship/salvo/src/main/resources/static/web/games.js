var data =
    fetch("/api/games")
    .then(function (response) {
        return response.json();
    })
    .then(function (data) {
        printData(data);
       /* document.addEventListener('click', function () {
                                  login(data);
        }, true);*/
    })
    .catch(function (error) {
        console.log(error);
    });

var scores =
    fetch("/api/leaderboard")
    .then(function (response) {
        return response.json();
    })
    .then(function (data) {
        printScores(data);
    })
    .catch(function (error) {
        console.log(error);
    });

let username = document.getElementById("username").value
let password = document.getElementById("password").value



function printData(data) {
    let el = document.getElementById("myList");
    console.log(data);
    for (var i = 1; i < data.length; i++) {
        let li = document.createElement("li");
        //li.setAttribute("class", "list-group-item disabled")
        let join = document.createElement("Button")
        join.innerHTML = "Join!"
        //join.setAttribute("class", "btn btn-primary")
        join.setAttribute("id", "game.html?gp="+data[i].id)

       if (data[i].gameplayers == null) {
            li.innerHTML = data[i].created;
        } else {
            var names = [];
            for (var k = 0; k < data[i].gameplayers.length; k++) {
                names.push(data[i].gameplayers[k].player.name)
            }
            li.innerHTML = data[i].created + "  " + names
        }
        let id = data[i].id
        join.addEventListener('click', function() {
                                       joinGame(id);
                                       });

        li.appendChild(join)
        el.appendChild(li);
     }
     //printlinks(data, username)
}

function printlinks(data, username){
    //console.log("links!!")
    if (username != ""){
     //console.log("links!!" + username)
    let linklist = document.getElementById("linklist")
    let divlist
    for (var i = 0; i < data.length; i++) {
            if (data[i].gameplayers != null)
            {
                for (var k = 0; k < data[i].gameplayers.length; k++) {
                    if (data[i].gameplayers[k].player.name == username){
                        divlist = document.createElement("li")
                        let a = document.createElement("a")
                        a.href = "game.html?gp="+data[i].id
                        divlist.innerHTML = a
                        linklist.appendChild(divlist)
                    }
                }
            }
        }
    }
}

function printScores(scores) {
    //console.log(scores)
    let ScoretableEl = document.getElementById("scoreTable")

    let headerEl = document.createElement("thead");

    let colEl = document.createElement("td");
    colEl.innerHTML = "Player name";
    headerEl.appendChild(colEl)

    let colEl2 = document.createElement("td");
    colEl2.innerHTML = "Wins";
    headerEl.appendChild(colEl2);

    let colEl3 = document.createElement("td");
    colEl3.innerHTML = "Losses";
    headerEl.appendChild(colEl3);

    let colEl4 = document.createElement("td");
    colEl4.innerHTML = "Draws";
    headerEl.appendChild(colEl4);

    let colEl5 = document.createElement("td");
    colEl5.innerHTML = "Total";
    headerEl.appendChild(colEl5);

    ScoretableEl.appendChild(headerEl);

    let leaderboard = document.getElementById("scoreTable");

    for (let i = 0; i < scores.player.length; i++) {
        let row = document.createElement("tr");
        let name = document.createElement("td");
        name.innerHTML = scores.player[i].name;
        row.appendChild(name);

        let wins = document.createElement("td");
        wins.innerHTML = scores.player[i].win;
        row.appendChild(wins);

        let loser = document.createElement("td");
        loser.innerHTML = scores.player[i].loser;
        row.appendChild(loser);

        let draw = document.createElement("td");
        draw.innerHTML = scores.player[i].draw;
        row.appendChild(draw);

        let total = document.createElement("td");
        total.innerHTML = scores.player[i].total;
        row.appendChild(total);

        leaderboard.appendChild(row);
    }
}

function login(data) {
    username = document.getElementById("username").value
    password = document.getElementById("password").value

    if (username == "" || password == "") {
        console.log("please fill the form")
    } else {
        let body = "name=" + username + "&pwd=" + password
        fetch("/api/login", {
            method: "POST",
            credentials: "same-origin",
            body: body,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        }).then(function (response) {
                //console.log(response)
                if (response.status == 200) {
                    console.log("logged in!")
                    //document.location.reload()
                    var node = document.getElementById("player")
                    var logout = document.getElementById("logout")
                    var login = document.getElementById("loginDiv")
                    node.innerHTML = "Hello! " + username ;
                    node.style.visibility = 'visible';
                    logout.style.visibility = 'hidden';
                    login.style.visibility = 'hidden'
                    printlinks(data, username)
                } else {
                    alert("wrong password or username  " + username + "--" + password)
                }
            },
            function (error) {
                error.message //=> String
            });
    }
}
function logout() {
            fetch("/api/logout", {
                    credentials: 'same-origin',
                    method: 'POST',
                })
                .then(function(response){
                    console.log(response)
                    if (response.status == 200) {
                       console.log("logged out!")
                       var node = document.getElementById("logout")
                       var player = document.getElementById("player")
                       if (player.style.visibility == 'hidden')
                       {
                         alert("You are not logged in yet!")
                       }
                       else{
                       node.innerHTML = "You are logged out!";
                       node.style.visibility = 'visible';
                       player.style.visibility = 'hidden';
                       var login = document.getElementById("loginDiv")
                       login.style.visibility = 'visible'
                       }
                    }
                },
                function (error) {
                    error.message //=> String
                });
}

function createUser() {
            let username = document.getElementById("username").value
            let password = document.getElementById("password").value
            if (this.username == "" || this.password == "") {
                alert("please fill the form")
            } else {
                body = "username=" + username + "&password=" + password
                fetch("/api/players", {
                    method: "POST",
                    body: body,
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    credentials: "same-origin"
                }).then(function (response) {
                    response.status
                    if (response.status == 201) {
                        alert("account created ")
                        login()
                    } else if(response.status == 409){
                        alert("user exists already" + username)
                    }
                }, function (error) {
                    error.message //=> String
                })
            }
        }

function createGame(){
            fetch("/api/newGame", {
                    method: "POST",
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    credentials: "same-origin"
                }).then(function (response) {
                        response.status
                        if (response.status == 201) {
                            return response.json().then((data) => { location.href  = data.link })
                        } else  {
                           return response.json().then((data) => { alert(data.error) })
                        }
                    }, function (error) {
                    error.message //=> String
                })
        }

function joinGame(id){
            body = "gameId="+id;
                fetch("/api/joinGame", {
                    method: "POST",
                    body: body,
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    credentials: "same-origin"
                }).then(function (response) {
                        response.status

                        if (response.status == 201) {
                            return response.json().then((data) => {
                            location.href = data.link })
                        } else  {
                           return response.json().then((data) => { alert(data.error) })
                        }
                    }, function (error) {
                    error.message //=> String
                })
        }

//function userCheck() {
//    $.get("../api/current")
//        .done(function (data) {
//            app.user = data;
//
//            if (app.user == "no User Found") {
//                app.loginDiv = true;
//                app.logoutDiv = false;
//                app.incoming = false;
//            } else {
//                alert("Welcome " + app.user)
//                app.loadData();
//                app.loadLeaeder();
//                app.loginDiv = false;
//                app.logoutDiv = true;
//            }
//        })
//        .fail(function (jqXHR, textStatus) {
//            app.error = "Failed: " + textStatus;
//            app.errordiv = true;
//        });
//}
