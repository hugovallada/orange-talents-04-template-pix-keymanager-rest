package br.com.zup.hugovallada.pix

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import javax.validation.Valid

@Controller("/api/v1/pix")
@Validated
class PixController {

    @Post
    fun cadastrarPix(@Body @Valid cadastroPixRequest: NovaChavePixRequest): HttpResponse<Any>{
        println(cadastroPixRequest)
        return HttpResponse.ok()
    }

}