package br.com.zup.hugovallada.pix

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TipoDeChaveRequestTest{

    @Nested
    inner class ChaveAleatoriaTest{
        private val tipoDeChave = TipoDeChaveRequest.CHAVE_ALEATORIA

        @Test
        fun `deve ser valido quando a chave aleatoria for nula ou vazia`(){

            assertTrue(tipoDeChave.valida(null))
            assertTrue(tipoDeChave.valida(""))
        }

        @Test
        internal fun `nao deve ser valido quando chave aleatoria possuir um valor`(){
            assertFalse(tipoDeChave.valida("um valor qualquer"))
        }
    }

    @Nested
    inner class ChaveEmailTest{
        private val tipoDeChave = TipoDeChaveRequest.EMAIL

        @Test
        internal fun `deve ser valido quando a chave for um email valido`() {
            assertTrue(tipoDeChave.valida("rafael.ponte@zup.com.br"))
        }

        @Test
        internal fun `nao deve ser valido quando o email nao for um email valido`() {
            assertFalse(tipoDeChave.valida("rafael.ponte2email.com"))
        }

        @Test
        internal fun `nao deve ser valido quando o email nao for informado`() {
            assertFalse(tipoDeChave.valida(null))
            assertFalse(tipoDeChave.valida(""))
        }
    }

    @Nested
    inner class ChaveCPFTest{
        private val tipoDeChave = TipoDeChaveRequest.CPF

        @Test
        internal fun `deve ser valido quando a chave for um cpf valido`() {
            assertTrue(tipoDeChave.valida("27935238014"))
        }

        @Test
        internal fun `nao deve ser valido quando o cpf for invalido`() {
            assertFalse(tipoDeChave.valida("1111111111"))
        }

        @Test
        internal fun `nao deve ser valido quando o cpf nao for informado`() {
            assertFalse(tipoDeChave.valida(null))
            assertFalse(tipoDeChave.valida(""))
        }
    }

    @Nested
    inner class ChaveCelularTest{
        private val tipoDeChave = TipoDeChaveRequest.TELEFONE_CELULAR

        @Test
        internal fun `deve ser valido quando a chave for um celular valido`() {
            assertTrue(tipoDeChave.valida("+16999998282"))
        }

        @Test
        internal fun `nao deve ser valido quando o celular for invalido`(){
            assertFalse(tipoDeChave.valida("celular"))
        }

        @Test
        internal fun `nao deve ser valido quando o celular nao for informado`() {
            assertFalse(tipoDeChave.valida(null))
            assertFalse(tipoDeChave.valida(""))
        }
    }

}