window.onload = function(){

    console.log("Loaded")

    $.get("http://localhost:8080/clients",(data)=>{

    data.forEach(client => {
        $("#clients").append("<tr><td>" + client.id + "</td><td>" + client.name + "</td><td>" + client.age + "</td><td><img src="+client.photo+"></td></tr>" )
    });
    })
}