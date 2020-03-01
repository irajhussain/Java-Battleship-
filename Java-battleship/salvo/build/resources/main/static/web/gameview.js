
var data =
    fetch("http://localhost:8080/api/gameview")
    .then(function (response) {
        return response.json();
    })
    .then(function (data) {
        printData(data);
    })
    .catch(function (error) {
        console.log(error);
    });

function printData(data) {

    let el = document.getElementById("myList");
    //console.log(data.length);
    for(var i=0; i<data.length; i++){
    let li = document.createElement("li");
    li.innerHTML = data[i].created;
    //console.log(data[i].gameplayers);
    console.log(data[i].gameplayers.player.length);

    if(data[i].gameplayers){
    console.log(data[i].gameplayers[0].player.length);
    for(var k=0; k<data[i].gameplayers[0].player.length; k++){
           li.innerHTML = data[i].gameplayers[0].player[k].name;
           console.log("not null");
           }
     }
    el.appendChild(li);
    }
}

