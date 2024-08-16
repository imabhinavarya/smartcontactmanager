console.log("this is script file")

const toggleSidebar = () => {
    if ($(".sidebar").is(":visible")) {
        $(".sidebar").css("display","none")
        $(".content").css("margin-left","0%");
    } else {
        $(".sidebar").css("display","block")
        $(".content").css("margin-left","20%");
    }
}

const search = () => {
    let query = $("#search-input").val();
    if(query==""){
        $(".search-result").hide();
    } else {

        //sending req to server
        let url = `http://localhost:8080/search/${query}`;
        fetch(url)
            .then((response) => {
            return response.json();
        }).then((data) => {
//            console.log(data);
            let text=`<div class='list-group'>`

            data.forEach((contact) =>{
                text+=`<a href='/user/contact/${contact.cId}' class='list-group-item list-group-item-action'>${contact.name}</a>`
            });

            text+=`</div>`

            $(".search-result").html(text);
            $(".search-result").show();
        });
    }

}

const paymentStart = () => {
    var amount = $("#payment_field").val();
    console.log(amount);
    if(amount=='' || amount==null) {
//        alert("amount is required !!");
        swal("Failed !!", "Amount is required", "error");
        return;
    }

    //code
    //will use ajax to send request to server to create Order - jquery

    $.ajax(
        {
            url:'/user/create_order',
            data:JSON.stringify({amount:amount,info:'order_request'}) ,
            contentType:'application/json',
            type:'POST',
            dataType:'json',
            success: function(response){
                console.log(response);
                if(response.status=='created') {
                    //open payment form
                    let options={
                        key:'rzp_test_tg9UVFB0LzVFXG',
                        amount:response.amount,
                        currency:'INR',
                        name:'Smart Contact Manager',
                        description:'Donation',
                        image:'',
                        order_id:response.id,
                        handler: function(response) {
                            console.log(response.razorpay_payment_id);
                            console.log(response.razorpay_order_id);
                            console.log(response.razorpay_signature);
                            console.log("Payment successful");

                            updatePaymentOnServer(
                                response.razorpay_payment_id,
                                response.razorpay_order_id,
                                "paid"
                            );

//                            alert("Congrats!!!, Payment successful!! ");
                            swal("Congrats!", "Payment successful!!", "success");
                        },
                        prefill: {
                            name:"",
                            email:"",
                            contact:"",
                        },
                        notes: {
                            address:"Abhinav",
                        },
                        theme: {
                            color: "#3399cc",
                        }
                    };

                    let rzp = new Razorpay(options);
                    rzp.on('payment.failed',function(response) {
                        console.log(response.error.code);
                        console.log(response.error.dscription);
                        console.log(response.error.source);
                        console.log(response.error.step);
                        console.log(response.error.reason);
                        console.log(response.error.metadata.order_id);
                        console.log(response.error.metadata.payment_id);
                        //alert("OOPS Payment Failed");
                        swal("Failed !", "Oops Payment Failed !!", "error");
                    });

                    rzp.open()
                }
            },
            error:function(error){
                //invoked when error
                console.log(error);
                alert("Something went wrong !!");
            }
        }
    )


};


function updatePaymentOnServer(payment_id,order_id,status){
    $.ajax({
        url:'/user/update_order',
        data:JSON.stringify({payment_id:payment_id, order_id:order_id, status:status}) ,
        contentType:'application/json',
        type:'POST',
        dataType:'json',
        sucess: function(response){
            swal("Congrats!", "Payment successful!!", "success");
        },
        error: function(error){
            swal("Failed!", "Payment successful!!, but error in saving on server", "error");
        }
    })
}