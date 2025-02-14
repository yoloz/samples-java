// gcc -shared -fPIC -o libnativec.so nativec.c

#include <string.h>
#include <assert.h>

int intMethod(int i)
{
    return i * i;
}

int booleanMethod(int b)
{
    return b > 0 ? 1 : 0;
}

char *stringMethod(char *str)
{
    assert(str);         // str的非空性
    char *ret = str;     // 定义一个ret保存最初的str
    while (*str != '\0') // 判断字符串是否结束
    {
        if ((*str >= 'a') && (*str <= 'z')) // 判断当前的字符是否是小写字母
        {
            *str = *str - 32; // 将其转化为大写字母
            str++;
        }
        else
        {
            str++;
        }
    }
    return ret; // 返回该字符串数组的首地址
}

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