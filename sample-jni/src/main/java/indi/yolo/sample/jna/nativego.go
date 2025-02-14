// go build -buildmode=c-shared -o libnativego.so nativego.go
package main

// #include <string.h>
import "C"
import "strings"

//export IntMethod
func IntMethod(i int) int {
	return i * i
}

//export BooleanMethod
func BooleanMethod(b int) int {
	if b > 0 {
		return 1
	} else {
		return 0
	}
}

//export StringMethod
func StringMethod(str *C.char) *C.char {
	chars := C.GoString(str)
	return C.CString(strings.ToUpper(chars))
}

func main() {}
