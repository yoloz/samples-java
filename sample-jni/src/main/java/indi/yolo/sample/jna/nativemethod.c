#include <string.h>

void my_strupr(char str[])
{
    int nNum;
    nNum = strlen(str);
    for (int i = 0; i < nNum; i++)
    {
        if (str[i] >= 'a' && str[i] <= 'z')
        {
            str[i] -= 32;
        }
    }
}

int intMethod(int i)
{
    return i * i;
}

int booleanMethod(int b)
{
    return b > 0 ? 1 : 0;
}

// char *stringMethod(char *str)
// {
//     char cap[128];
//     strcpy(cap, str);
//     // free(str);
//     my_strupr(cap);
//     return cap;
// }

int intArrayMethod(int *array)
{
    int i, sum = 0;
    int len = sizeof(array);
    for (i = 0; i < len + 1; i++)
    {
        sum += array[i];
    }
    // free(array);
    return sum;
}