function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, '\\$&');
    var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
        results = regex.exec(url);
    res = decodeURIComponent(results[2].replace(/\+/g, ' '));
    //console.log(res);
    return res;
}

var curr_player = getParameterByName('gp')
var gid = getParameterByName('gg')

var gamestatus = ["not started", "ship placing", "wait for opponent ships", "salvo firing", "waiting for opponent turn", "game over"]
var curr_status = localStorage.getItem("curr_status");
var turnNumber = 0
var ship_types = [
        {Type: "Carrier", Length: 5, Count: 0},
        {Type: "Battleship", Length: 4, Count: 0},
        {Type: "Submarine", Length: 3, Count: 0},
        {Type: "Destroyer", Length: 3, Count: 0},
        {Type: "Patrolboat", Length: 2, Count: 0}
    ]
var opp
var myships
var oppships
var mysalvos
var oppsalvos
var myturn
var locations = []

//Ship table header row
var a = ["a", "b", "c", "d", "e", "f", "g", "h", "i", "j"]

async function fetchData(obj) {
    url = "/api/gameview/" + obj;
    fetch(url, {
                    credentials: 'same-origin',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                }).then(function (response) {
                    response.status
                    return response.json().then((data) => {
                    populateData(data, turnNumber)
                    //printme()
                    if(turnNumber == 0){
                        localStorage.setItem("curr_status", "not started")
                        printUsertable(data)
                        localStorage.setItem("myturn", 1)
                        localStorage.setItem("mysunk", 0)
                    }
                    turnNumber += 1
                    if(turnNumber == 1){
                        fetchData(opp)
                        localStorage.setItem("oppsunk", 0)
                    }
                    if(turnNumber == 2){
                        myships = JSON.parse(localStorage.getItem("myships"))
                        oppships = JSON.parse(localStorage.getItem("oppships"))
                        mysalvos = JSON.parse(localStorage.getItem("mysalvos"))
                        oppsalvos = JSON.parse(localStorage.getItem("oppsalvos"))
                        printData(myships,oppships,mysalvos,oppsalvos)
                    }
                    })
                },
                function (error) {
                    alert(error.message)
                })
 }              //fetching json with cur and op det

function populateData(data, turn){
    if(turn == 0){
        for (let i = 0; i < data.gameplayers.length; i++){
            if(data.gameplayers[i].id == curr_player){
                console.log("fetch for curr_player")
                localStorage.setItem("myships", JSON.stringify(data.ships))
                localStorage.setItem("mysalvos", JSON.stringify(data.salvos))
                //myships = data.ships
                //mysalvos = data.salvos
                localStorage.setItem("curr_player", curr_player);
            }
            else {
                opp = data.gameplayers[i].id
                localStorage.setItem("opp", opp);
            }
        }
    }
    if(turn == 1){
            console.log("fetch for opp")
            localStorage.setItem("oppships", JSON.stringify(data.ships))
            localStorage.setItem("oppsalvos", JSON.stringify(data.salvos))
            localStorage.setItem("myturn", 0)
            localStorage.setItem("sunk", 0)
     }
}           //saving in local storage

function printme(){
    console.log(JSON.parse(localStorage.getItem("myships")))
    console.log(JSON.parse(localStorage.getItem("mysalvos")))
    console.log(localStorage.getItem("opp"));
}

fetchData(curr_player)

function printData(myships,oppships,mysalvos,oppsalvos) {
    //add ships
    if(localStorage.getItem("curr_status") == "ship placing"){
        printShips()
        updateShips(myships, oppships)
    }

    // add salvos
    if (localStorage.getItem("curr_status") == "salvo firing"){
        hideshipbuttons()
        printSalvos()
        printStatus()
        updateSalvos(mysalvos)
        updateStatus(myships, oppships, mysalvos, oppsalvos)
    }
}   //print salvos and ships and turn table on current status

function printUsertable(data){
    let UsertableEl = document.getElementById("userTable")
    if(data.gameplayers.length == 2){
        localStorage.setItem("curr_status", "ship placing")
        for (let i = 0; i < data.gameplayers.length; i++) {
                    let rowEl = document.createElement("tr");
                    if (data.gameplayers[i].id == curr_player) {
                        let colEl = document.createElement("td");
                        colEl.innerHTML = "You " + data.gameplayers[i].player.name;
                        rowEl.appendChild(colEl);
                    } else {
                        let colEl = document.createElement("td");
                        colEl.innerHTML = "Opponent " + data.gameplayers[i].player.name;
                        opp = data.gameplayers[i].id;
                        rowEl.appendChild(colEl);
                        //fetchOpp(opp, data)
                     }
                    UsertableEl.appendChild(rowEl);
                }
    }
    else if(data.gameplayers.length < 2){
        alert("Not enough players in game yet")
    }
    else{
        alert("Too many players! Only two allowed")
    }
}               //user and oppponent name

function printShips(){
    let ShiptableEl = document.getElementById("shipTable")
    let ShipdetailsEl = document.getElementById("shipDetails")
    //ship table header row
    for (let i = 0; i < 11; i++) {
        let headerEl = document.createElement("th");
        headerEl.innerHTML = i;
        ShiptableEl.appendChild(headerEl);
    }

    //ship table header column and set id
    for (let i = 0; i < 10; i++) {
        let rowEl = document.createElement("tr");
        let colEl = document.createElement("td");
        colEl.innerHTML = a[i];
        rowEl.appendChild(colEl);
        for (let j = 1; j < 11; j++) {
            let cell = document.createElement("td");
            let id = "ship" + a[i] + j;
            cell.setAttribute("id", id);
            rowEl.appendChild(cell);
        }
        ShiptableEl.appendChild(rowEl);
    }

    //ship details table header
    let head = document.createElement("thead")
    let c = document.createElement("td")
    let d = document.createElement("td")
    let e = document.createElement("td")
    let f = document.createElement("td")
    let g = document.createElement("td")
    c.innerHTML = "Type"
    head.appendChild(c)
    d.innerHTML = "Length"
    head.appendChild(d)
    e.innerHTML = "Count"
    head.appendChild(e)
    f.innerHTML = "Color"
    head.appendChild(f)
    g.innerHTML = "Action"
    head.appendChild(g)
    shipDetails.appendChild(head)

    //print ship details
    for (let q = 0; q < ship_types.length; q++)
    {
        let r = document.createElement("tr")
        let i = document.createElement("td")
        i.innerHTML = ship_types[q].Type
        r.appendChild(i)

        let j = document.createElement("td")
        j.innerHTML = ship_types[q].Length
        r.appendChild(j)

        let k = document.createElement("td")
        k.setAttribute("id",ship_types[q].Type)
        r.appendChild(k)

        let l = document.createElement("td")
        l.setAttribute("id","color"+ship_types[q].Type)
        r.appendChild(l)

        let m = document.createElement("td")
        n = document.createElement("Button")
        n.setAttribute("id","action"+ship_types[q].Type)
        //l.style.visibility = "hidden"
        n.innerHTML = "Add Ship!"
        n.addEventListener('click', function() { AddShip(ship_types[q].Type, false);});
        n.setAttribute("class", "btn btn-warning")
        m.appendChild(n)
        r.appendChild(m)

        shipDetails.appendChild(r)
    }

}                       //ship grid

function updateShips(myships, oppships){
    for (let i = 0; i < myships.length; i++) {
        let ship = myships[i];
        color = getRandomColor()

        for (let j = 0; j < ship.locations.length; j++) {
            let loc = "ship"+ ship.locations[j];
            let cell2 = document.getElementById(loc);
            cell2.style.backgroundColor = color;
        }
        let col = document.getElementById("color"+ship.type)
        col.style.backgroundColor = color
        ship_increment(ship.type, ship.locations)
    }
    if(myships.length == 3){
        hideshipbuttons()
        if (oppships.length == 3){
            localStorage.setItem("curr_status", "salvo firing")
            console.log(localStorage.getItem("curr_status"))
        }
        else{
            localStorage.setItem("curr_status", "waiting for opponent ships")
            alert("Your ship placement done!\n" + status)
        }

    }
}

function ship_increment(type, locations){
        //console.log("in inc"+ locations)
        let objIndex = ship_types.findIndex((obj => obj.Type == type));

        ship_types[objIndex].Count += 1

        var ship_cnt = document.getElementById(type)
        ship_cnt.innerHTML = ship_types.find((obj => obj.Type == type)).Count

        var action = document.getElementById("action"+type)
        action.innerHTML = "Modify Ship!"
        action.addEventListener('click', function() { shipModify(type,locations);});
        action.setAttribute("class", "btn btn-warning")
        //action.style.visibility = "visible"
}

function hideshipbuttons(){
    let ship_div = document.getElementById("ship_edit")
    ship_div.style.display = "none"
    for(let i = 0; i < ship_types.length; i++){
        let btn = document.getElementById("action"+ship_types[i].Type)
        btn.style.display = "none"
    }

}

function get_ship_count(type){
    let objIndex = ship_types.findIndex((obj => obj.Type == type));
    var ship_cnt = document.getElementById(type)
    if (ship_cnt.innerHTML == "")
        return 0;
     return parseInt(ship_types[objIndex].Count)
}

function printSalvos(){
    let SalvotableEl = document.getElementById("salvoTable")
    for (let i = 0; i < 11; i++) {
        let headerEl = document.createElement("th");
        headerEl.innerHTML = i;
        SalvotableEl.appendChild(headerEl);
    }

    //salvo table header column and set id
    for (let i = 0; i < 10; i++) {
        let rowEl = document.createElement("tr");
        let colEl = document.createElement("td");
        colEl.innerHTML = a[i];
        rowEl.appendChild(colEl);
        for (let j = 1; j < 11; j++) {
            let cell = document.createElement("td");
            let id = "salvo" + a[i] + j;
            cell.setAttribute("id", id);
            cell.setAttribute('onclick', 'addSalvos('+i+','+j+')');
            rowEl.appendChild(cell);
        }
        SalvotableEl.appendChild(rowEl);
    }

}

function updateSalvos(mysalvos){
    if (mysalvos){
            for(let i = 0; i < mysalvos.length; i++){
                var salvo = mysalvos[i];
                console.log(salvo)
                for (let j = 0; j < salvo.locations.length; j++){
                            let cell = document.getElementById("salvo"+ salvo.locations[j]);
                            cell.style.backgroundColor = "yellow";
                            cell.innerHTML = salvo.turn;
                            var last_turn = parseInt(localStorage.getItem("myturn"))
                            console.log(salvo.turn + "--" + last_turn)
                            if(salvo.turn > last_turn){
                                console.log("updating turn")
                                localStorage.setItem("myturn", parseInt(salvo.turn));
                                console.log(localStorage.getItem("myturn"))
                            }
                     }
                }
            }
            else{
                localStorage.setItem("myturn", parseInt("0"))
            }

}

function printStatus(){
    let StatustableEl = document.getElementById("statusTable")

    //Current game status header
    let headstatus = document.createElement("thead")
    let turn = document.createElement("td")
    let myhits = document.createElement("td")
    let myleft = document.createElement("td")
    let opphits = document.createElement("td")
    let oppleft = document.createElement("td")
    headstatus.appendChild(turn)
    headstatus.appendChild(myhits)
    headstatus.appendChild(myleft)
    headstatus.appendChild(opphits)
    headstatus.appendChild(oppleft)

    turn.innerHTML = "Turn"
    myhits.innerHTML = "My hits"
    myleft.innerHTML="My Left"
    opphits.innerHTML = "Opp hits"
    oppleft.innerHTML="Opp Left"

    /*let row = document.createElement("tr")
                let hits = document.createElement("td")
                hits.innerHTML="Hits"
                row.appendChild(hits)

                row.appendChild(left)
                let hits2 = document.createElement("td")
                hits2.innerHTML="Hits"
                row.appendChild(hits2)
                let left2 = document.createElement("td")
                left2.innerHTML="Left"
                row.appendChild(left2)*/
    statusTable.appendChild(headstatus)
}                   //status table

function updateStatus(myships, oppships, mysalvos, oppsalvos){
    var turnno = 1
    var moreturns = true
    var oppturns = true
    var myturns = true

    if(localStorage.getItem("curr_status") == "salvo firing")
     {
        while (moreturns){
            var row = document.createElement("tr")
            var turn = document.createElement("td")
            turn.innerHTML = turnno
            row.appendChild(turn)
            var myhits = document.createElement("td")
            var myleft = document.createElement("td")
            row.appendChild(myhits)
            row.appendChild(myleft)
            var opphits = document.createElement("td")
            var oppleft = document.createElement("td")
            row.appendChild(opphits)
            row.appendChild(oppleft)
            if (oppturns)
            {
                oppturns = findoppsalvos(turnno, oppsalvos, myships, myhits, myleft)
            }
            if (myturns){
                myturns = findmysalvos(turnno, mysalvos, oppships, opphits, oppleft)
                //localStorage.setItem("myturn", parseInt(localStorage.getItem("myturn")) + 1)
            }
            if(oppturns || myturns){
                moreturn = true
            }
            else {
                moreturns = false
            }
            turnno += 1
            statusTable.appendChild(row)
        }
        var mywin = checkgame(myships, oppsalvos, "mysunk")
            if (mywin == "lost"){
                var winner = localStorage.getItem("opp")
                var loser = localStorage.getItem("curr_player")
                localStorage.setItem("curr_status", "game over")
                console.log(localStorage.getItem("curr_status"))
                addgamescores(winner, loser)
                alert("You lost! \n Game Over")
            }
        var oppwin = checkgame(oppships, mysalvos, "oppsunk")
            if(oppwin == "lost"){
                var winner = localStorage.getItem("curr_player")
                var loser = localStorage.getItem("opp")
                localStorage.setItem("curr_status", "game over")
                console.log(localStorage.getItem("curr_status"))
                addgamescores(winner, loser)
                alert("You win! \n Game Over")
       }
    }
} // status table

//add scores to the players for this game
function addgamescores(winner, loser){
    bodyMessage  = "winnerId=" + winner + "&LoserId=" + loser
    url = "/api/addScores/" + gid;
                fetch(url, {
                    credentials: 'same-origin',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: bodyMessage
                }).then(function (response) {
                    response.status
                    console.log(response)
                    if (response.status == 201) {
                        return response.json().then((data) => {
                            console.log("Scores added")
                            })
                    } else {
                        return response.json().then((data) => {
                            alert(data.error)
                        })
                    }
                }, function (error) {
                    alert(error.message)

                })
}

function findoppsalvos(turn, oppsalvos, myships, opphits, oppleft){
    for (let i = 0; i < oppsalvos.length; i++){
        if (oppsalvos[i].turn == turn){
            var myshiphits = gethitships(oppsalvos[i], myships)
            for (let a = 0; a < myshiphits.count; a++){
                opphits.appendChild(document.createTextNode(myshiphits.type))
                oppleft.innerHTML = myshiphits.left
             }
        }
    }

    /* Mark the affected ship locations */
    for (let i = 0; i < oppsalvos.length; i++){
        var salvo = oppsalvos[i]
        for (let j = 0; j < salvo.locations.length; j++){
            let hit = document.getElementById("ship"+ salvo.locations[j]);
            hit.innerHTML = "X";
    }
    }
    if (turn >= oppsalvos.length){
        return false
    }
    return true
}

function findmysalvos(turn, mysalvos, oppships, myhits, myleft){
for (let i = 0; i < mysalvos.length; i++){
        if(mysalvos[i].turn == turn){
            var oppshiphits = gethitships(mysalvos[i], oppships)
            for (let a = 0; a < oppshiphits.count; a++){
                myhits.appendChild(document.createTextNode(oppshiphits.type))
                myleft.innerHTML = oppshiphits.left
             }
        }
     }
     if (turn >= mysalvos.length){
         return false
     }
     return true
}

function checkgame(ships, salvos, sunk){
    var sunk_ships = parseInt(localStorage.getItem(sunk))

    for(let i = 0; i < ships.length; i++){
        var cur_hits = 0
        for(let j = 0; j < salvos.length; j++){
            cur_hits += intersect(ships[i].locations, salvos[j].locations).length
            }
            if (cur_hits == ships[i].locations.length){
                sunk_ships += 1
                localStorage.setItem(sunk, parseInt(sunk_ships))
                console.log(ships[i].type)
            }
    }
    if (parseInt(localStorage.getItem(sunk)) == ships.length){
            console.log(sunk_ships)
            localStorage.setItem("curr_status" , "game over")
            return "lost"
     }
}

function findsunkships(ship, salvos){
    var hits = 0
    for (let i = 0; i < salvos.length; i++){
            hits += intersect(ship.locations, salvo.locations)
    }
    if(hits == ship.length){

    }
}

function addSalvos(i, j){
cell = document.getElementById("salvo"+a[i]+j)
    if (cell.style.backgroundColor == ""){
        cell.style.backgroundColor = getRandomColor();
        locations.push(a[i]+j)
    }
    else if(cell.innerHTML == ""){
        for (l = 0; l <locations.length; l++){
            if (locations[l] == a[i]+j)
            locations.splice(l,1)
           }
        cell.style.backgroundColor = "";
        ship = document.getElementById("ship"+a[i]+j)
        ship.innerHTML = ""
        cell.innerHTML = ""
    }
    else{
        alert("Cannot undo previous turns")
    }
    var salvo_confirm = document.getElementById("salvo")
    var fun = "salvoSender()"
    salvo_confirm.setAttribute("onclick", fun)
}

function AddShip(type, mod){
    let ship_div = document.getElementById("ship_edit")
    let edit_tbl = document.createElement("table")
    let row = document.createElement("tr")

    let o = document.createElement("input")
    o.setAttribute("value","Enter ship start position")
    let p = document.createElement("td")
    p.appendChild(o)
    row.appendChild(p)

    let radio_col = document.createElement("td")
    var dirList = document.createElement("select");
    dirList.id = "shipDir";
    radio_col.appendChild(dirList);

    //Create and append the options
    var h = document.createElement("option");
    h.value = "horizontal";
    h.text = "horizontal";
    dirList.appendChild(h);

    var v = document.createElement("option");
    v.value = "vertical";
    v.text = "vertical";
    dirList.appendChild(v);

    row.appendChild(radio_col)

    let ship_t = document.createElement("td")
    //Create and append select list
    var selectList = document.createElement("select");
    selectList.id = "shipType";
    ship_t.appendChild(selectList);

    //Create and append the options
    var option = document.createElement("option");
    option.value = type
    option.text = type
    selectList.appendChild(option);
    row.appendChild(ship_t)

    let m = document.createElement("td")
    n = document.createElement("Button")
    n.innerHTML = "Confirm!"
    n.addEventListener('click', function() { shipChecker(selectList.value, o.value, dirList.value, mod); });
    m.appendChild(n)
    row.appendChild(m)

    edit_tbl.appendChild(row)
    ship_div.appendChild(edit_tbl)
}

function makeRadioButton(name, value, text) {

    var label = document.createElement("label");
    var radio = document.createElement("input");
    radio.type = "radio";
    radio.name = name;
    radio.value = value;

    label.appendChild(radio);

    label.appendChild(document.createTextNode(text));
    return label;
}

function gethitships(salvo, ships){
    var type = []
    var left = []
    var count = 0
    for (let i = 0; i < ships.length; i++){
        let ship = ships[i]
        let hits = intersect(salvo.locations, ship.locations)
        if (hits.length > 0){
            type.push(ship.type)
            if (parseInt(ship.locations.length) - hits.length > 0){
                left.push(parseInt(ship.locations.length) - hits.length)
            }
            else{
                left.push("sunk")
            }
            count += 1
        }
    }
    return {
            type: type,
            left: left,
            count: count
        };
}

function intersect(a, b) {
    var t;
    if (b.length > a.length) t = b, b = a, a = t; // indexOf to loop over shorter
    return a.filter(function (e) {
        return b.indexOf(e) > -1;
    });
}

function getRandomColor() {
  var letters = '0123456789ABCDEF';
  var color = '#';
  for (var c = 0; c < 6; c++) {
    color += letters[Math.floor(Math.random() * 16)];
  }
  return color;
}

function shipAdder(bodyMessage, num, type) {
            url = "/api/addShips/" + num;

            fetch(url, {
                credentials: 'same-origin',
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: bodyMessage
            }).then(function (response) {
                response.status
                console.log(response)
                if (response.status == 201) {
                    return response.json().then((data) => {
                        console.log("in adder"+ data.locations)
                        var color = getRandomColor()
                        ship_increment(type, data.locations)
                        document.getElementById("ship_edit").innerHTML = ""
                        for(i=0;i<data.locations.length; i++){
                            testColor("ship", locations[i], color, type)
                        }
                    })
                } else {
                    //console.log(response.json())
                    return response.json().then((data) => {
                        alert(data.error)
                        //location.reload()
                    })
                }
            }, function (error) {
                alert(error.message)

            })
 }

function shipMod(bodyMessage, num, type) {
            this.number = Number(this.number)
            url = "/api/modShips/" + num;

            fetch(url, {
                credentials: 'same-origin',
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: bodyMessage
            }).then(function (response) {
                response.status
                console.log(response)
                if (response.status == 201) {
                    return response.json().then((data) => {
                        //console.log("in adder"+ data.locations)
                        var color = getRandomColor()
                        ship_increment(type, data.locations)
                        document.getElementById("ship_edit").innerHTML = ""
                        for(i=0;i<data.locations.length; i++){
                            testColor("ship", locations[i], color, type)
                        }
                    })
                } else {
                    //console.log(response.json())
                    return response.json().then((data) => {
                        alert(data.error)
                        //location.reload()
                    })
                }
            }, function (error) {
                alert(error.message)

            })
 }

function testColor(name, coordinate, color, type, turnValue) {
                var cell = document.getElementById(name+coordinate)
                cell.style.backgroundColor = color
                var cell2 = document.getElementById("color"+type)
                cell2.style.backgroundColor = color

                /*for (k = 0; k < table.rows[0].cells.length; k++) {
                    if (coordinate[0] == table.th1[k]) {
                        t = Number(coordinate.slice(1))
                        table.rows[t].cells[k + 1].style.backgroundColor = color
                        if (turnValue != undefined)
                            table.rows[t].cells[k + 1].innerHTML = turnValue;
                    }
                }*/
 }

var a = ["a", "b", "c", "d", "e", "f", "g", "h", "i", "j"]
function shipChecker(type, str_loc, dir, mod) {
          //console.log(type)
            if (str_loc == null) {
                return alert("select a starting location")
            }
            if (str_loc == null) {
                return alert("select a direction")
            }
            if (type == null) {
                return alert("select a ship to add")
            }
            var table = document.getElementById("shipTable")
            rowNumber = (table.rows.length)
            columnNumber = (table.rows[0].cells.length)
            columnIndex = str_loc[1];
            //console.log(a)
            rowIndex = a.findIndex((element => element == str_loc[0]))
            locations = []
            ship_len = ship_types.find((obj => obj.Type == type)).Length
            //console.log(rowIndex, columnIndex)
            if (dir == "vertical") {
                if ((rowNumber - rowIndex) < ship_len) {
                    alert("No space left in the column")
                } else {
                    var total = parseInt(rowIndex) + parseInt(ship_len)
                    //console.log(total)
                    for (i = rowIndex; i < total; i++) {
                    console.log("ship"+a[i]+i)
                        let cell = document.getElementById("ship"+a[i]+columnIndex)
                        if (!(cell.style.backgroundColor == "")) {
                            locations = []
                            alert("from checker one or more locations occupied")
                        }
                        locations.push(a[i]+columnIndex);
                        //console.log(str_loc[0]+i);
                    }
                    console.log(locations);
                }
            }
            if (dir == "horizontal") {
                if ((columnNumber - columnIndex) < ship_len) {
                    return alert("No space left in the row")
                }
                else {
                    var total = parseInt(columnIndex) + parseInt(ship_len)
                    for (i = columnIndex; i < total; i++) {
                    //console.log("ship"+a[rowIndex]+i)
                        let cell = document.getElementById("ship"+a[rowIndex]+i)
                        if (!(cell.style.backgroundColor == "")) {
                            locations = []
                            alert("from checker one or more locations occupied")
                        }
                        locations.push(str_loc[0]+i)
                    }
                    console.log(locations);
                }
            }
            body = "shipClass=" + type + "&locations=" + locations + "&direction=" + dir
            if(mod == false){
                shipAdder(body, curr_player, type)
            }
            else {
                shipMod(body,  curr_player, type)
            }
}

function shipModify(type,locations){
    let objIndex = ship_types.findIndex((obj => obj.Type == type));
    ship_types[objIndex].Count -= 1

    for (let i = 0; i < locations.length; i ++){
        var cell = document.getElementById("ship"+locations[i])
        cell.style.backgroundColor = ""
    }
    AddShip(type, true)
}

function salvoSender(){
            salvosToSend = locations
            body = "locations=" + salvosToSend
            locations = []
            url = "/api/addSalvos/" + curr_player + "/" + parseInt(parseInt(localStorage.getItem("myturn")) + 1);
            fetch(url, {
                credentials: 'same-origin',
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: body
            }).then(function (response) {
                response.status
                if (response.status == 201) {
                    return response.json().then((data) => {
                    //console.log(data)
                    localStorage.setItem("myturn", parseInt(localStorage.getItem("myturn")) + 1)
                    console.log(localStorage.getItem("myturn"))
                    for(i=0; i<data.locations.length; i++){
                        var locations = data.locations[i]
                        var row = locations[0]
                        var col = locations[1]
                        var cell = document.getElementById("salvo"+row+col)
                        cell.innerHTML = data.turn
                    }
                    })
                } else {
                    return response.json().then((data) => {
                        alert(data.error)
                        location.reload()
                    })
                }
            }, function (error) {
                alert(error.message)
            })
 }

/*function printOpp(oppdata){
    console.log(oppdata)
    if (oppdata.salvos){
        for(i = 0; i < oppdata.salvos.length; i++){
                let row = document.createElement("tr")
                var salvo = oppdata.salvos[i];
                let hits = 0
                for (let j = 0; j < salvo.locations.length; j++){
                    let hit = document.getElementById("ship"+ salvo.locations[j]);
                    hit.innerHTML = "X";
                    let turn = document.createElement("td")
                    turn.innerHTML = salvo.turn;
                    row.appendChild(turn)
                    if(hit.style.backgroundColor != ""){
                        hits += 1
                    }
                    var ships = gethitships(salvo, myships)
                    var type = ships.type
                    var left = ships.left
                    console.log(type)
                    console.log(left)
                }
                statusTable.appendChild(row)
            }
    }
}*/

/*async function fetchOpp(obj, curr_data) {
    url = "http://localhost:8080/api/gameview/" + obj;
        fetch(url, {
                        credentials: 'same-origin',
                        method: 'POST',
                        headers: {
                            'Accept': 'application/json',
                            'Content-Type': 'application/x-www-form-urlencoded'
                        }
                    }).then(function (response) {
                        response.status
                        //console.log(response)
                        return response.json().then((data) => {
                        updateStatus(data, curr_data)
                        })
                    },
                    function (error) {
                        alert(error.message)
                    })
}*/

/*function shipSender() {
            let str_loc = document.getElementById()
            for (i = 0; i < this.dummyList.length; i++) {
                body = "shipClass=" + this.dummyList[i].name + "&locations=" + this.dummyList[i].locations + "&direction=" + this.dummyList[i].direction
                shipAdder(body)
            }
            this.dummyList = [];
}*/

/*function shipPlacer() {
     for (i = 0; i < this.playerlist.ships.length; i++) {
           delete this.shipList[this.playerlist.ships[i].type];
           for (j = 0; j < this.playerlist.ships[i].locations.length; j++) {
               coordinate = this.playerlist.ships[i].locations[j]
               this.ships.push(coordinate)
           }
     }
     this.shipParser()
}

function shipParser() {
    var table = document.getElementById("shipTable")
    for (i = 0; i < this.ships.length; i++) {
        coordinate = this.ships[i]
        this.testColor("shipTable", coordinate, "red")
    }
}

function consoler (json) {
    this.playerlist = json
    id = this.playerlist.id
    this.shipPlacer();
    //this.playerSeperator();
    //this.statusReportGetter();
}

function loadData (url) {
    fetch(url, {
            method: "GET",
            credentials: "include",
        })
        .then(r => r.json())
        .then(json => consoler(json))
        .catch(e => console.log(e));
}

function locationGetter () {
    var currentLocation = window.location;
    num = currentLocation.search.split('=').pop();
    this.number = num
    url = "../api/gameview/" + num;
    this.loadData(url)
}

function checkopp(){
    if (this.number>0){
      fetch("/api/checkOpponent/"+this.number, {
            method: "GET",
            credentials: "include",
        })
        .then(r => r.json())
        .then(json => {

                       app.checkertext=json.status
                      app.checker=true;
          if (json.status!="waiting for opponent" && app.opponent==null){
              app.locationGetter()
          }

          if(json.hasOwnProperty("ships")){
              app.salvoLength=json.ships
          }
          if(json.status=="you lost"||json.status=="you win"||json.status=="its a tie"){
              clearInterval(app.timer)
              app.locationGetter();
          }
                      })
        .catch(e => console.log(e));
      }
    }

function dummyColor () {
            var table = document.getElementById("shipTable")
            for (j = 0; j < this.dummyList.length; j++) {
                for (k = 0; k < this.dummyList[j].locations.length; k++) {
                    coordinate = this.dummyList[j].locations[k];
                    for (i = 0; i < this.th1.length; i++) {
                        if (coordinate[0] == this.th1[i]) {
                            t = Number(coordinate.slice(1))
                            table.rows[t].cells[i + 1].style.backgroundColor = "green"
                        }
                    }
                }
            }
 }*/