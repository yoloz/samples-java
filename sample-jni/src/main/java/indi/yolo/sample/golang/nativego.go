// go build -buildmode=c-shared -o libnativego.so nativego.go
package main

// #include <string.h>
// #include <stdlib.h>
import "C"
import (
	"strings"
	"unsafe"
)

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
	defer C.free(unsafe.Pointer(str))
	cstr := C.CString(strings.ToUpper(chars))
	// defer C.free(unsafe.Pointer(cstr))
	return cstr
}

func main() {}
