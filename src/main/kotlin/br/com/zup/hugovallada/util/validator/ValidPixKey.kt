package br.com.zup.hugovallada.util.validator

import br.com.zup.hugovallada.TipoDeChave
import br.com.zup.hugovallada.pix.NovaChavePixRequest
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidPixKeyValidator::class])
annotation class ValidPixKey(
    val message: String = "Não é uma chave pix válida",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class ValidPixKeyValidator : ConstraintValidator<ValidPixKey, NovaChavePixRequest> {
    override fun isValid(
        value: NovaChavePixRequest?,
        annotationMetadata: AnnotationValue<ValidPixKey>,
        context: ConstraintValidatorContext
    ): Boolean {
        if(value?.tipoDeChave == null) return false
        if(value.valor.isNullOrBlank() && value.tipoDeChave != TipoDeChave.CHAVE_ALEATORIA) return false

        if(value.tipoDeChave == TipoDeChave.EMAIL){
            return value.valor!!.matches("^[0-9]{11}\$".toRegex()) && CPFValidator().run {
                initialize(null)
                isValid(value.valor, null)
            }
        }

        if(value.tipoDeChave == TipoDeChave.TELEFONE_CELULAR){
            return value.valor!!.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }

        if(value.tipoDeChave == TipoDeChave.CPF){
            return CPFValidator().run {
                initialize(null)
                isValid(value.valor, null)
            }
        }

        return value.valor.isNullOrBlank() && value.tipoDeChave == TipoDeChave.CHAVE_ALEATORIA

    }

}

