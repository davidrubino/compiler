package trad.syntax.ast.impl;

public class CodeExtracts {
    private CodeExtracts() {
    }

    public static final String ITOA = "// FONCTIONS PRé-DéFINIES EN LANAGAGE D'ASSEMBLAGE\n" +
            "\n" +
            "// char *itoa(int i, char *p, int b);\n" +
            "//\n" +
            "// i entier à convertir\n" +
            "// p pointeur du tampon déjà alloué en mémoire où copier la chaîne de caractères\n" +
            "// b base de numération utilisée (de 2 à 36 inclus car il n'y a que 36 chiffres; par exemple: 2, 4, 8, 10, 16)\n" +
            "//\n" +
            "// Convertit un entier en chaîne de caractères codée en ASCII\n" +
            "// (cette fonction fait partie de la bibliothèque standard portable C stdlib et est normalement écrite en C).\n" +
            "// Limitation ici: b doit être pair.\n" +
            "// Retourne le pointeur sur la chaîne de caractère\n" +
            "//\n" +
            "// Ce programme terminera automatiquement la chaîne de caractères par NUL;\n" +
            "// le tampon devrait avoir une taille suffisante (par exemple sizeof(int)*8+1 octets pour b=2)\n" +
            "// Si la base = 10 et que l'entier est négatif la chaîne de caractères est précédée d'un signe moins (-);\n" +
            "// pour toute autre base, la valeur i est considérée non signée.\n" +
            "// Les 36 chiffres utilisables sont dans l'ordre: 0, 1, 2,..., 9, A, B, C, ... , Z .\n" +
            "// Aucune erreur n'est gérée.\n" +
            "     \n" +
            "\n" +
            "ITOA_I      equ 4      // offset du paramètre i\n" +
            "ITOA_P      equ 6      // offset du paramètre p\n" +
            "ITOA_B      equ 8      // offset du paramètre b\n" +
            "\n" +
            "ASCII_MINUS equ 45     // code ASCII de -\n" +
            "ASCII_PLUS  equ 43     // code ASCII de +\n" +
            "ASCII_SP    equ 32     // code ASCII d'espace SP\n" +
            "ASCII_0     equ 48     // code ASCII de zéro (les autres chiffres jusqu'à 9 suivent dans l'ordre)\n" +
            "ASCII_A     equ 65     // code ASCII de A (les autres lettres jusqu'à Z suivent dans l'ordre alphabétique)\n" +
            "NUL         equ 0\n" +
            "// LNK: crée environnement du main pour permettre des variables locales \n" +
            "// mais sans encore les réserver\n" +
            "itoa_   stw bp, -(sp)\n" +
            "        ldw bp, sp\n" +
            "\n" +
            "// récupération des paramètres depuis pile vers registres\n" +
            "        ldw r0, (bp)ITOA_I    // r0 = i    \n" +
            "        ldw r1, (bp)ITOA_B    // r1 = b\n" +
            "        \n" +
            "// gère le signe: normalement itoa gère des int c'est à dire des entiers signés, \n" +
            "// mais en fait seulement pour b=10;\n" +
            "// dans ce cas calcule le signe dans r3 et charge r0 avec la valeur absolue de i\n" +
            "        ldq 0, r3\n" +
            "        ldq 10, wr            // 10 -> wr\n" +
            "        cmp r1, wr            // charge les indicateurs de b - 10\n" +
            "        bne NOSIGN-$-2        // si non égal (donc si b != 10) saute en NOSIGN, sinon calcule signe\n" +
            //            "        ldq ASCII_PLUS, r3    // charge le code ASCII du signe plus + dans r3\n" +
            "        tst r0                // charge les indicateurs de r0 et donc de i\n" +
            //            "        bge POSIT-$-2         // saute en POSIT si i >= 0\n" +
            "        bge NOSIGN-$-2         // saute en NOSIGN si i >= 0\n" +
            "        neg r0, r0            // change le signe de r0\n" +
            "        ldq ASCII_MINUS, r3   // charge le code ASCII du signe moins - dans r3\n" +
            "                              // r3 = code ASCII de signe: SP pour aucun, - ou +\n" +
            "\n" +
            "\n" +
            "// convertit l'entier i en chiffres et les empile de droite à gauche\n" +
            "NOSIGN  ldw r2, r0            // r2 <- r0\n" +
            "CNVLOOP ldw r0, r2            // r0 <- r2\n" +
            "   \n" +
            "   // effectue \"créativement\" la division par b supposé pair (car l'instruction div est hélas signée ...)\n" +
            "   // d=2*d' , D = d * q + r  , D = 2*D'+r\" , D' = d' * q + r' => r = 2*r'+r\"\n" +
            "   // un bug apparaît avec SRL R0, R0 avec R0 = 2 : met CF à 1 !!\n" +
            "        srl r1, r1            // r1 = b/2\n" +
            "        ani r0, r4, #1        // ANd Immédiate entre r0 et 00...01 vers r4:\n" +
            "                              // bit n°0 de r0 -> r4; r4 = reste\" de r0/2\n" +
            "        srl r0, r0            // r0 / 2 -> r0\n" +
            "        div r0, r1, r2        // quotient = r0 / r1 -> r2, reste' = r0 % r1 -> r0\n" +
            "        shl r0, r0            // r0 = 2 * reste'\n" +
            "        add r0, r4, r0        // r0 = reste = 2 * reste' + reste\" => r0 = chiffre\n" +
            "        shl r1, r1            // r1 = b\n" +
            "\n" +
            "        adq -10, r0           // chiffre - 10 -> r0 \n" +
            "        bge LETTER-$-2        // saute en LETTER si chiffre >= 10\n" +
            "        adq 10+ASCII_0, r0    // ajoute 10 => r0 = chiffre, ajoute code ASCII de 0 \n" +
            "                              // => r0 = code ASCII de chiffre\n" +
            "        bmp STKCHR-$-2        // saute en STKCHR \n" +
            "\n" +
            "LETTER  adq ASCII_A, r0       // r0 = ASCII(A) pour chiffre = 10, ASCII(B) pour 11 ...\n" +
            "                              // ajoute code ASCII de A => r = code ASCII de chiffre\n" +
            "STKCHR  stw r0, -(sp)         // empile code ASCII du chiffre \n" +
            "                              // (sur un mot complet pour pas désaligner pile)\n" +
            "        tst r2                // charge les indicateurs en fonction du quotient ds r2)\n" +
            "        bne CNVLOOP-$-2       // boucle si quotient non nul; sinon sort\n" +
            "\n" +
            "// les caractères sont maintenant empilés : gauche en haut et droit en bas\n" +
            "\n" +
            "// recopie les caractères dans le tampon dans le bon ordre: de gauche à droite\n" +
            "        ldw r1, (bp)ITOA_P    // r1 pointe sur le début du tampon déjà alloué \n" +
            "        tst r3\n" +
            "        beq 2                 // passe l'instruction suivante si r3 = 0\n" +
            "        stb r3, (r1)+         // copie le signe dans le tampon\n" +
            "CPYLOOP ldw r0, (sp)+         // dépile code du chiffre gauche (sur un mot) dans r0\n" +
            "        stb r0, (r1)+         // copie code du chiffre dans un Byte du tampon de gauche à droite\n" +
            "        cmp sp, bp            // compare sp et sa valeur avant empilement des caractères qui était bp\n" +
            "        bne CPYLOOP-$-2       // boucle s'il reste au moins un chiffre sur la pile\n" +
            "        ldq NUL, r0           // charge le code du caractère NUL dans r0\n" +
            "        stb r0, (r1)+         // sauve code NUL pour terminer la chaîne de caractères\n" +
            "\n" +
            "// termine\n" +
            "        ldw r0, (bp)ITOA_P    // retourne le pointeur sur la chaîne de caractères\n" +
            "\n" +
            "    // UNLINK: fermeture de l'environnement de la fonction itoa\n" +
            "        ldw sp, bp            // sp <- bp : abandonne infos locales; sp pointe sur ancinne valeur de bp\n" +
            "        ldw bp, (sp)+         // dépile ancienne valeur de bp dans bp; sp pointe sur adresse de retour\n" +
            "\n" +
            "        rts                   // retourne au programme appelant\n" +
            "";
    public static final String POW = "// Fonction puissance\n" +
            "\n" +
            "pow_     stw bp, -(sp)\n" +
            "         ldw bp, sp\n" +
            "         stw r1, -(sp)\n" +
            "         stw r2, -(sp)\n" +
            "         ldq 1, r0     // r0 : résultat\n" +
            "         ldw r1, (bp)4 // r1 : nombre\n" +
            "         ldw r2, (bp)6 // r2 : puissance\n" +
            "pow_loop tst r2\n" +
            "         ble pow_end-$-2\n" +
            "         mul r0, r1, r0\n" +
            "         adq -1, r2\n" +
            "         bmp pow_loop-$-2\n" +
            "pow_end  ldw r2, (sp)+\n" +
            "         ldw r1, (sp)+\n" +
            "         ldw bp, (sp)+\n" +
            "         rts\n";

    public static final String ATOI = "// Fonction atoi\n" +
            "// int atoi(char *s)\n" +
            "// r0 : résultat\n" +
            "// r1 : adresse caractère courant\n" +
            "// r2 : code ascii caractère courante\n" +
            "// r3 : signe (1 = positif, -1 = négatif)\n" +
            "\n" +
            "atoi_   stw bp, -(sp)\n" +
            "        ldw bp, sp\n" +
            "        stw r1, -(sp)\n" +
            "        stw r2, -(sp)\n" +
            "        stw r3, -(sp)\n" +
            "        // Initialisations\n" +
            "        ldq 0, r0       // résultat\n" +
            "        ldq 0, r2\n" +
            "        ldw r1, (bp)4   // argument s\n" +
            "        ldb r2, (r1)+\n" +
            "        ldq 1, r3\n" +
            "\n" +
            "        beq atoi_c - $ - 2 // test si fin de chaine\n" +
            "        // Détermination signe\n" +
            "        ldq 45, wr\n" +
            "        cmp r2, wr\n" +
            "        bne 4\n" +
            "            neg r3, r3    // si négatif\n" +
            "            ldb r2, (r1)+ // lire le caractère après le -\n" +
            "\n" +
            "        ldq 10, wr\n" +
            "        // boucle de traitement\n" +
            "atoi_l  tst r2\n" +
            "        beq atoi_c - $ - 2 // test si fin de chaine\n" +
            "        adq -48, r2\n" +
            "        mul r0, wr, r0\n" +
            "        add r0, r2, r0\n" +
            "        ldb r2, (r1)+\n" +
            "        bmp atoi_l - $ - 2\n" +
            "atoi_c  mul r0, r3, r0\n" +
            "        ldw r3, (sp)+\n" +
            "        ldw r2, (sp)+\n" +
            "        ldw r1, (sp)+\n" +
            "        ldw bp, (sp)+\n" +
            "        rts";
}
